package com.retoTenpo.reto.advice;

import com.retoTenpo.reto.controller.response.ErrorResponse;
import com.retoTenpo.reto.repository.HistoryRepository;
import com.retoTenpo.reto.repository.model.History;
import com.retoTenpo.reto.service.exception.InvalidAnswerException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  private final HistoryRepository historyRepository;

  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(IllegalArgumentException ex) {
    ErrorResponse error = new ErrorResponse("BAD_REQUEST", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
    log.error("Excepción no capturada: " + ex.getClass().getName(), ex);
    ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "Ocurrió un error inesperado.");
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
  }

  @ExceptionHandler(ServerWebInputException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleServerWebInputException(ServerWebInputException ex) {
    ex.printStackTrace(); // <-- esto va a mostrar que adentro hay un NPE
    ErrorResponse error = new ErrorResponse("BAD_REQUEST", "Entrada inválida: " + ex.getReason());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException ex, ServerWebExchange exchange) {
    log.info("Exception WebExchangeBindException {}",ex.getMessage());
    String errorMessage = ex.getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining(", "));

    String path = exchange.getRequest().getPath().toString();
    String method = exchange.getRequest().getMethod().name();
    String requestBody = (String) exchange.getAttributes().get("requestBody");
    // Guardar en historial
    return Mono.fromCallable(() -> historyRepository.save(History.builder()
            .answer(null)
            .date(LocalDateTime.now())
            .errorMessage(errorMessage)
            .endPoint(path)
            .method(method)
            .parameters(requestBody)
            .status("ERROR")
            .build()))
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSubscribe(sub -> log.info("📌 Starting delayed history registration..."))
        .doOnSuccess(h -> log.info("✅ History registered successfully"))
        .doOnError(throwable -> log.error("❌ Failed to register history: {}", throwable.getMessage()))
        .then(Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(errorMessage)
                .build()
        )));
  }

  @ExceptionHandler(InvalidAnswerException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleInvalidAnswer(InvalidAnswerException ex, ServerWebExchange exchange) {
    log.info("Exception InvalidAnswerException {}",ex.getMessage());
    String errorMessage = ex.getMessage();
    String path = exchange.getRequest().getPath().toString();
    String method = exchange.getRequest().getMethod().name();
    String requestBody = (String) exchange.getAttributes().get("requestBody"); // si lo tienes seteado previamente

    // Guardar en historial
    return Mono.fromCallable(() -> historyRepository.save(History.builder()
            .answer(null)
            .date(LocalDateTime.now())
            .errorMessage(errorMessage)
            .endPoint(path)
            .method(method)
            .parameters(requestBody)
            .status("ERROR")
            .build()))
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSubscribe(sub -> log.info("📌 Starting delayed history registration..."))
        .doOnSuccess(h -> log.info("✅ History registered successfully"))
        .doOnError(throwable -> log.error("❌ Failed to register history: {}", throwable.getMessage()))
        .then(Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(errorMessage)
                .build()
        )));
  }
}
