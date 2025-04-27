package com.retoTenpo.reto.service.impl;

import com.retoTenpo.reto.controller.request.ValuesRequest;
import com.retoTenpo.reto.controller.response.HistoryResponse;
import com.retoTenpo.reto.controller.response.PagedResponse;
import com.retoTenpo.reto.controller.response.ValuesResponse;
import com.retoTenpo.reto.repository.HistoryRepository;
import com.retoTenpo.reto.repository.SessionTemplateRepository;
import com.retoTenpo.reto.repository.model.History;
import com.retoTenpo.reto.repository.model.Session;
import com.retoTenpo.reto.service.ConsultService;
import com.retoTenpo.reto.service.exception.InvalidAnswerException;
import com.retoTenpo.reto.service.exception.SessionNotFoundException;
import com.retoTenpo.reto.webclient.ExternalApiWebClient;
import com.retoTenpo.reto.webclient.util.JsonConvert;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsultServiceImpl implements ConsultService {

  private final HistoryRepository historyRepository;

  private final ExternalApiWebClient externalApiWebClient;

  private final SessionTemplateRepository sessionTemplateRepository;

  @Override
  public Mono<ValuesResponse> calculateSum(ValuesRequest request, ServerWebExchange httpRequest) {
    log.info("ingresando: {}",request);
    BigDecimal num1 = request.getNum1();
    BigDecimal num2 = request.getNum2();

    // If both are present, proceed with the sum and normal validations
    BigDecimal result = num1.add(num2);

    // Validation for numeric(38,2) limits
    String plainString = result.stripTrailingZeros().toPlainString();
    int digitsBeforeDecimal = plainString.contains(".") ? plainString.indexOf(".") : plainString.length();
    int digitsAfterDecimal = plainString.contains(".") ? plainString.length() - plainString.indexOf(".") - 1 : 0;

    if (digitsBeforeDecimal + digitsAfterDecimal > 38 || digitsAfterDecimal > 2) {
      return Mono.error(new InvalidAnswerException("El resultado excede el límite de numeric(38, 2)"));
    }

    // Everything is OK, save with status OK
    History history = ConsultServiceBuild.buildCommonHistory(num1, num2, httpRequest.getRequest().getMethod().toString(), httpRequest)
        .answer(result)
        .status("OK")
        .build();

    UUID uuid = createUuidIdentifier(num1.toString(), num2.toString());

    return handleNewSession(history, num1.toString(), num2.toString(), uuid);
  }

  private Mono<ValuesResponse> handleNewSession(History history, String num1, String num2, UUID uuid) {
    BigDecimal originalAnswer = history.getAnswer();
    return externalApiWebClient.getPercentage().flatMap(externalApiResponse -> {

      BigDecimal percentage = externalApiResponse.getRandom();

      // Calculate increment: originalAnswer * percentage / 100
      BigDecimal increment = originalAnswer.multiply(percentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      BigDecimal newAnswer = originalAnswer.add(increment);

      history.setAnswer(newAnswer); // Assign new value

      UUID uuidSession = createUuidIdentifier(num1, num2);

      // 🔄 Asynchronous save in the background with a delay
      Mono.fromCallable(() -> historyRepository.save(history))
          .subscribeOn(Schedulers.boundedElastic())
          .delaySubscription(Duration.ofSeconds(20))
          .doOnSubscribe(sub -> log.info("📌 Starting delayed history registration..."))
          .doOnSuccess(h -> log.info("✅ History registered successfully"))
          .doOnError(ex -> log.error("❌ Failed to register history: {}", ex.getMessage()))
          .subscribe();

      // ✅ Only waits for the session
      return sessionTemplateRepository.saveSesion(Session.builder()
              .guuid(uuidSession)
              .percentage(percentage)
              .build())
          .map(sess -> ValuesResponse.builder()
              .answer(newAnswer)
              .build()
          );
    }).onErrorResume(e -> {
      log.warn("⚠️ Falló externalApiWebClient.getPercentage(): {}. Recuperando desde Redis...", e.getMessage());
      return sessionTemplateRepository.findSesionByGuuid(uuid)
          .flatMap(fallbackSession -> {
            log.info("Initial session: {}", JsonConvert.serializeObject(fallbackSession));
            if (fallbackSession.getGuuid() == null) {
              return Mono.error(new SessionNotFoundException("No se encontró sesión válida en Redis"));
            }
            // Calculate increment: originalAnswer * percentage / 100
            BigDecimal increment = originalAnswer.multiply(fallbackSession.getPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal newAnswer = originalAnswer.add(increment);

            history.setAnswer(newAnswer); // Asignar nuevo valor

            // 🔄 Asynchronous background save with delay
            Mono.fromCallable(() -> historyRepository.save(history))
                .subscribeOn(Schedulers.boundedElastic())
                .delaySubscription(Duration.ofSeconds(20))
                .doOnSubscribe(sub -> log.info("📌 Starting delayed history registration..."))
                .doOnSuccess(h -> log.info("✅ History registered successfully"))
                .doOnError(ex -> log.error("❌ Failed to register history: {}", ex.getMessage()))
                .subscribe();

            return Mono.just(ValuesResponse.builder()
                .answer(newAnswer)
                .build());
          });
    });
  }

  private UUID createUuidIdentifier(String num1, String num2) {
    String concatenatedString = num1 + num2;
    return UUID.nameUUIDFromBytes(concatenatedString.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public Mono<PagedResponse<HistoryResponse>> getHistory(PageRequest pageRequest) {
    return Mono.fromCallable(() -> historyRepository.findAll(pageRequest))
        .subscribeOn(Schedulers.boundedElastic())
        .map(page -> new PagedResponse<>(
            page.getContent().stream().map(this::mapToResponse).toList(),
            pageRequest.getPageNumber() + 1,
            pageRequest.getPageSize(),
            page.getTotalElements(),
            page.getTotalPages()
        ));
  }

  private HistoryResponse mapToResponse(History history) {
    return HistoryResponse.builder()
        .date(history.getDate())
        .endPoint(history.getEndPoint())
        .parameters(history.getParameters())
        .answer(history.getAnswer())
        .error(history.getErrorMessage())
        .build();
  }
}
