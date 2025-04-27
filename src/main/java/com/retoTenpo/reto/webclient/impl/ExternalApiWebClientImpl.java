package com.retoTenpo.reto.webclient.impl;

import com.retoTenpo.reto.webclient.ExternalApiWebClient;
import com.retoTenpo.reto.webclient.response.ExternalApiResponse;
import com.retoTenpo.reto.webclient.util.WebClientUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ExternalApiWebClientImpl implements ExternalApiWebClient {

  private final WebClient getPercentage;

  private final ReactiveCircuitBreakerFactory cbFactory;

  private final CircuitBreakerRegistry circuitBreakerRegistry;

  @Value("${mock.api.percentage.enabled}")
  private boolean mockEnabled;

  @Value("${mock.api.percentage.value}")
  private BigDecimal mockValue;

  @Override
  public Mono<ExternalApiResponse> getPercentage() {
    if (mockEnabled) {
      return Mono.just(ExternalApiResponse.builder()
          .random(mockValue)
          .build());
    }

    ReactiveCircuitBreaker rcb = cbFactory.create("slow");
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("getPercentage");
    log.info("Estado del CircuitBreaker: {}", circuitBreaker.getState());
    return getPercentage
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/csrng.php")
            .queryParam("min", 1)
            .queryParam("max", 100)
            .build())
        .retrieve()
        .bodyToMono(ExternalApiResponse[].class)
        .filter(array -> array.length > 0)
        .map(array -> array[0])
        .transformDeferred(responseMono -> rcb.run(responseMono, throwable ->
            WebClientUtil.fallBack("ExternalApiWebClientImpl", "getPercentage", throwable)))
        .doOnNext(response -> log.info("Response recibido: {}", response))
        .log();
  }
}
