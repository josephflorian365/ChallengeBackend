package com.retoTenpo.reto.webclient.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@UtilityClass
public class WebClientUtil {
  public static <T> Mono<T> fallBack(String className, String identifier, Throwable t) {
    log.error("{}.{} - request, cause - {}, throwable class - {}", className, identifier, t.getMessage(), t.getClass().getName(), t);
    return Mono.error(t);
  }
}
