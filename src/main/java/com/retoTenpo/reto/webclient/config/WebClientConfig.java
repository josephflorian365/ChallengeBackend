package com.retoTenpo.reto.webclient.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final ExternalApiEnvironmentConfig externalApiEnvironmentConfig;

  @Value("${webclient.connect.time-out.millis}")
  private Integer webclientConnectTimeOut;

  @Value("${webclient.read.time-out.seconds}")
  private Long webclientReadTimeOut;

  @Value("${webclient.write.time-out.seconds}")
  private Long webclientWriteTimeOut;

  @Bean("getPercentage")
  public WebClient getPercentage(WebClient.Builder webClientBuilder) {
    return webClientBuilder.baseUrl(externalApiEnvironmentConfig.getUrl())
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webclientConnectTimeOut)
            .doOnConnected(connection ->
                connection.addHandlerLast(new ReadTimeoutHandler(webclientReadTimeOut, TimeUnit.SECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(webclientWriteTimeOut, TimeUnit.SECONDS)))))
        .build();
  }
}
