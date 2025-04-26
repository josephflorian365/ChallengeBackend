package com.retoTenpo.reto.webclient.response;

import java.math.BigDecimal;
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
public class ExternalApiResponse {
  private String status;
  private int min;
  private int max;
  private BigDecimal random;
}
