package com.retoTenpo.reto.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name="history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class History {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name="date")
  private LocalDateTime date;

  @Column(name="endpoint")
  private String endPoint;

  @Column(name="method")
  private String method;

  @Column(name="parameters")
  private String parameters;

  @Column(name="answer")
  private BigDecimal answer;

  @Column(name = "status")
  private String status;

  @Column(name = "error")
  private String errorMessage;
}
