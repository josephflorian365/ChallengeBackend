package com.retoTenpo.reto.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HomeRouter {

  @Bean
  public RouterFunction<ServerResponse> index() {
    return route(
        GET("/"),
        req -> ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build()
    );
  }
}
