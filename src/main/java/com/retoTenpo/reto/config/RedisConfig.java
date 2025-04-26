package com.retoTenpo.reto.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.retoTenpo.reto.repository.model.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.data.redis.password}")
  private String redisPass;

  @Bean
  public LettuceConnectionFactory lettuceConnectionFactory() {
    var redisStandaloneConfig = new RedisStandaloneConfiguration();
    redisStandaloneConfig.setHostName(redisHost);
    redisStandaloneConfig.setPort(redisPort);
    redisStandaloneConfig.setPassword(redisPass);
    LettuceClientConfiguration lettuceConnectionFactory = LettuceClientConfiguration.builder().build();
    return new LettuceConnectionFactory(redisStandaloneConfig, lettuceConnectionFactory);
  }

  @Bean
  public ReactiveRedisTemplate<String, Session> reactiveSessionRedisTemplate(
      LettuceConnectionFactory factory) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    Jackson2JsonRedisSerializer<Session> valueSerializer = new Jackson2JsonRedisSerializer<>(
        objectMapper,Session.class);
    RedisSerializationContext.RedisSerializationContextBuilder<String, Session> builder =
        RedisSerializationContext.newSerializationContext(valueSerializer);
    RedisSerializationContext<String, Session> context = builder.value(valueSerializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
  }

  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
    stringRedisTemplate.setConnectionFactory(connectionFactory);
    return stringRedisTemplate;
  }
}
