package com.retoTenpo.reto.repository;

import com.retoTenpo.reto.repository.model.Session;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SessionTemplateRepository {

  private final SessionRepository sessionRepository;

  @Value("${spring.cache.redis.key-prefix}")
  private String keyPrefix;

  @Value("${spring.cache.redis.time-to-live}")
  private Long expitarionTimeRedis;

  public Mono<Session> findSesionByGuuid(UUID id) {
    return Mono.justOrEmpty(sessionRepository.findById(id))
        .switchIfEmpty(Mono.just(Session.builder().guuid(null).build()))
        .flatMap(Mono::just);
  }

  public Mono<Session> saveSesion(Session sesion) {
    String key = keyPrefix + sha256(sesion.getGuuid().toString());
    log.info("key save : {}", key);
    log.info("Sesion a guardar : {}", sesion);
    sesion.setExpireTime(expitarionTimeRedis);

    return Mono.just(sessionRepository.save(sesion));
  }

  public String sha256(String keyRedis) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(keyRedis.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();

      for (int i = 0; i < hash.length; ++i) {
        String hex = Integer.toHexString(255 & hash[i]);
        if (hex.length() == 1) {
          hexString.append('0');
        }

        hexString.append(hex);
      }

      return hexString.toString();
    } catch (Exception var9) {
      throw new IllegalArgumentException();
    }
  }

}
