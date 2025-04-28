package com.retoTenpo.reto.util;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RequestCachingFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    // Evitar GET u otros métodos sin body
    if (request.getMethod() == null ||
        request.getMethod().matches("GET") ||
        request.getHeaders().getContentLength() == 0 ||
        request.getHeaders().getContentLength() == -1) {
      return chain.filter(exchange);
    }

    return DataBufferUtils.join(request.getBody())
        .flatMap(dataBuffer -> {
          byte[] bytes = new byte[dataBuffer.readableByteCount()];
          dataBuffer.read(bytes);
          DataBufferUtils.release(dataBuffer);

          // Store the body in the exchange attributes
          String requestBody = new String(bytes, StandardCharsets.UTF_8);
          exchange.getAttributes().put("requestBody", requestBody);

          // Clone the body into a new DataBuffer
          Flux<DataBuffer> cachedBody = Flux.defer(() -> {
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return Mono.just(buffer);
          });

          // Create a new request that uses the cloned body
          ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
              return cachedBody;
            }
          };

          return chain.filter(exchange.mutate().request(mutatedRequest).build());
        });
  }
}
