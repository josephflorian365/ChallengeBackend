package com.retoTenpo.reto.webclient.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.support.base.external-api")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalApiEnvironmentConfig {
  private String url;
}
