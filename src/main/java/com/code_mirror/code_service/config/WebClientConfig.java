package com.code_mirror.code_service.config;

import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient openAIWebClient() {
        return WebClient.builder()
                .baseUrl("https://openrouter.ai/api")
                .build();
    }

    @Bean
    public RedisTemplate<String, CodingProblem> codingProblemRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CodingProblem> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(CodingProblem.class));
        return template;
    }

}
