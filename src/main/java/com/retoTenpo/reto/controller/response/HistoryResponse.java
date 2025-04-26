package com.retoTenpo.reto.controller.response;

import java.time.LocalDate;
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
  private LocalDate date;
  private String endPoint;
  private String parameters;
  private String answer;
}
