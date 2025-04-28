package com.retoTenpo.reto.controller;

import com.retoTenpo.reto.controller.request.ValuesRequest;
import com.retoTenpo.reto.controller.response.HistoryResponse;
import com.retoTenpo.reto.controller.response.PagedResponse;
import com.retoTenpo.reto.controller.response.ValuesResponse;
import com.retoTenpo.reto.service.ConsultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RequestsController {

  private final ConsultService consultService;

  @PostMapping("/sum")
  public Mono<ValuesResponse> calculateSum(
      @RequestBody @Valid ValuesRequest request,
      @RequestParam(defaultValue = "true") boolean mockEnabled,
      ServerWebExchange httpRequest) {
    return consultService.calculateSum(request, mockEnabled,httpRequest);
  }

  @GetMapping("/history")
  public Mono<PagedResponse<HistoryResponse>> getHistory(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    PageRequest pageRequest = PageRequest.of(page - 1, size);
    return consultService.getHistory(pageRequest);
  }
}
