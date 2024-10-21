package com.j1p3ter.userserver.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(); // Redis 서버 정보
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setUsername(username);
        configuration.setPassword(password);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // LocalDateTime 같은 타입을 위한 모듈 자동 등록
        objectMapper.deactivateDefaultTyping(); // @class 제거

        // Jackson2JsonRedisSerializer 설정
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 키는 String, 값은 JSON 형식으로 처리
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(60))
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java()));

        Map<String, RedisCacheConfiguration> customConfigurations = new HashMap<>();

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(customConfigurations)
                .build();
    }
}
