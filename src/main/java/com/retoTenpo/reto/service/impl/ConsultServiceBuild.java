package com.retoTenpo.reto.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retoTenpo.reto.repository.model.History;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.server.ServerWebExchange;

public class ConsultServiceBuild {

  public static History.HistoryBuilder buildCommonHistory(BigDecimal num1, BigDecimal num2, String endpoint, ServerWebExchange httpRequest) {
    return History.builder()
        .endPoint(httpRequest.getRequest().getURI().getPath())
        .date(LocalDateTime.now()) // Usás LocalDateTime en lugar de LocalDate si querés incluir hora
        .method(endpoint)
        .parameters(toJson(num1,num2));
  }

  public static String toJson(BigDecimal num1, BigDecimal num2) {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> params = new HashMap<>();
    params.put("num1", num1);
    params.put("num2", num2);
    try {
      return mapper.writeValueAsString(params);
    } catch (JsonProcessingException e) {
      return "{}";
    }
  }
}
