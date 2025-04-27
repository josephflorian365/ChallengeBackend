package com.retoTenpo.reto.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class HistoryResponse {
  private LocalDateTime date;
  private String endPoint;
  private String parameters;
  private BigDecimal answer;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String error;
}
