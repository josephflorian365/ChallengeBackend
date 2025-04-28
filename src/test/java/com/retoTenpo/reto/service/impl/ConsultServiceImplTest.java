package com.retoTenpo.reto.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.retoTenpo.reto.controller.request.ValuesRequest;
import com.retoTenpo.reto.controller.response.HistoryResponse;
import com.retoTenpo.reto.controller.response.PagedResponse;
import com.retoTenpo.reto.controller.response.ValuesResponse;
import com.retoTenpo.reto.repository.HistoryRepository;
import com.retoTenpo.reto.repository.SessionTemplateRepository;
import com.retoTenpo.reto.repository.model.History;
import com.retoTenpo.reto.repository.model.Session;
import com.retoTenpo.reto.service.exception.SessionNotFoundException;
import com.retoTenpo.reto.webclient.ExternalApiWebClient;
import com.retoTenpo.reto.webclient.response.ExternalApiResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ConsultServiceImplTest {
  @Mock
  private HistoryRepository historyRepository;
  @Mock
  private ExternalApiWebClient externalApiWebClient;
  @Mock
  private SessionTemplateRepository sessionTemplateRepository;


  @InjectMocks
  private ConsultServiceImpl consultService;

  @Test
  void givenNum1Num2_whenCalculateSum_thenReturnAnswer() {
    when(externalApiWebClient.getPercentage(any(Boolean.class)))
        .thenReturn(Mono.just(ExternalApiResponse.builder().random(BigDecimal.TEN).build()));
    when(sessionTemplateRepository.saveSesion(any(Session.class)))
        .thenReturn(Mono.just(Session.builder().build()));
    ServerWebExchange exchange = mock(ServerWebExchange.class);
    ServerHttpRequest request = mock(ServerHttpRequest.class);
    URI mockUri = URI.create("/test-path");

    when(exchange.getRequest()).thenReturn(request);
    when(request.getMethod()).thenReturn(HttpMethod.POST);
    when(request.getURI()).thenReturn(mockUri);
    Mono<ValuesResponse> response = consultService.calculateSum(ValuesRequest.builder()
        .num1(BigDecimal.TEN)
        .num2(BigDecimal.TEN)
        .build(), Boolean.TRUE,exchange);
    StepVerifier.create(response)
        .expectNextMatches(valuesResponse -> {
          BigDecimal sum = BigDecimal.TEN.add(BigDecimal.TEN);
          BigDecimal addPercentage = sum.multiply((BigDecimal.TEN).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
          BigDecimal answer = sum.add(addPercentage);
          assertEquals(answer, valuesResponse.getAnswer());
          return true;
        })
        .expectComplete()
        .verify();
  }

  @Test
  void givenNum1Num2_whenErrorGetPercentage_thenFindSessionByGuuidAndReturnAnswer() {
    when(externalApiWebClient.getPercentage(any(Boolean.class)))
        .thenReturn(Mono.error(new RuntimeException("Simulated 500 error from external API")));
    when(sessionTemplateRepository.findSesionByGuuid(any(UUID.class)))
        .thenReturn(Mono.just(Session.builder().guuid(UUID.randomUUID()).percentage(BigDecimal.TEN).build()));
    ServerWebExchange exchange = mock(ServerWebExchange.class);
    ServerHttpRequest request = mock(ServerHttpRequest.class);
    URI mockUri = URI.create("/test-path");

    when(exchange.getRequest()).thenReturn(request);
    when(request.getMethod()).thenReturn(HttpMethod.POST);
    when(request.getURI()).thenReturn(mockUri);
    Mono<ValuesResponse> response = consultService.calculateSum(ValuesRequest.builder()
        .num1(BigDecimal.TEN)
        .num2(BigDecimal.TEN)
        .build(), Boolean.TRUE, exchange);
    StepVerifier.create(response)
        .expectNextMatches(valuesResponse -> {
          BigDecimal sum = BigDecimal.TEN.add(BigDecimal.TEN);
          BigDecimal addPercentage = sum.multiply((BigDecimal.TEN).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
          BigDecimal answer = sum.add(addPercentage);
          assertEquals(answer, valuesResponse.getAnswer());
          return true;
        })
        .expectComplete()
        .verify();
  }

  @Test
  void givenNum1Num2_whenErrorGetPercentage_thenFindSessionByGuuidAndReturnException() {
    when(externalApiWebClient.getPercentage(any(Boolean.class)))
        .thenReturn(Mono.error(new RuntimeException("Simulated 500 error from external API")));
    when(sessionTemplateRepository.findSesionByGuuid(any(UUID.class)))
        .thenReturn(Mono.just(Session.builder().build()));
    ServerWebExchange exchange = mock(ServerWebExchange.class);
    ServerHttpRequest request = mock(ServerHttpRequest.class);
    URI mockUri = URI.create("/test-path");

    when(exchange.getRequest()).thenReturn(request);
    when(request.getMethod()).thenReturn(HttpMethod.POST);
    when(request.getURI()).thenReturn(mockUri);
    Mono<ValuesResponse> response = consultService.calculateSum(ValuesRequest.builder()
        .num1(BigDecimal.TEN)
        .num2(BigDecimal.TEN)
        .build(), Boolean.TRUE, exchange);
    // Act & Assert
    StepVerifier.create(response)
        .expectErrorMatches(throwable -> throwable instanceof SessionNotFoundException)
        .verify();
  }

  @Test
  void givenDataBase_whenGetHistory_thenReturnPageResponse() {
    // Arrange (Given)
    PageRequest pageRequest = PageRequest.of(0, 10);

    History mockHistory = History.builder()
        .answer(BigDecimal.TEN)
        .parameters("params")
        .endPoint("/test")
        .date(LocalDateTime.now())
        .errorMessage(null)
        .build();
    List<History> historyList = List.of(mockHistory);

    Page<History> mockPage = new PageImpl<>(historyList, pageRequest, historyList.size());

    when(historyRepository.findAll(pageRequest)).thenReturn(mockPage);

    // Act (When)
    Mono<PagedResponse<HistoryResponse>> result = consultService.getHistory(pageRequest);

    // Assert (Then)
    StepVerifier.create(result)
        .assertNext(response -> {
          assertEquals(1, response.getContent().size());
          assertEquals(1, response.getTotalElements());
          assertEquals(1, response.getTotalPages());
          assertEquals(1, response.getPage()); // 0 (base) + 1
          assertEquals(10, response.getSize());
        })
        .verifyComplete();
  }
}