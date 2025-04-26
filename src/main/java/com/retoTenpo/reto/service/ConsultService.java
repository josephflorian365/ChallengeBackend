package com.retoTenpo.reto.service;

import com.retoTenpo.reto.controller.request.ValuesRequest;
import com.retoTenpo.reto.controller.response.HistoryResponse;
import com.retoTenpo.reto.controller.response.ValuesResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ConsultService {
  Mono<ValuesResponse> calculateSum(ValuesRequest request, ServerWebExchange httpRequest);

  Mono<HistoryResponse> getHistory();
}
