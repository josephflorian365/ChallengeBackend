package com.retoTenpo.reto.webclient;

import com.retoTenpo.reto.webclient.response.ExternalApiResponse;
import reactor.core.publisher.Mono;

public interface ExternalApiWebClient {
  Mono<ExternalApiResponse> getPercentage();
}
