package com.retoTenpo.reto.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
@RedisHash(value = "session")
public class Session implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Indexed
  private UUID guuid;
  private BigDecimal percentage;
  @Builder.Default
  private LocalDateTime creationDate = LocalDateTime.now();
  @TimeToLive
  private Long expireTime;
}
