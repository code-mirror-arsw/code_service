package com.code_mirror.code_service.config;


import com.code_mirror.code_service.infrastructure.repository.CodingProblem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class WebClientConfigTest {

    private WebClientConfig config;

    @BeforeEach
    public void setUp() {
        config = new WebClientConfig();
        try {
            var field = WebClientConfig.class.getDeclaredField("openApiUrl");
            field.setAccessible(true);
            field.set(config, "https://api.openai.com/v1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOpenAIWebClient() {
        WebClient client = config.openAIWebClient();
        assertThat(client).isNotNull();
    }

    @Test
    public void testCodingProblemRedisTemplate() {
        RedisConnectionFactory mockFactory = Mockito.mock(RedisConnectionFactory.class);

        RedisTemplate<String, CodingProblem> template = config.codingProblemRedisTemplate(mockFactory);

        assertThat(template).isNotNull();
        assertThat(template.getConnectionFactory()).isEqualTo(mockFactory);
        assertThat(template.getKeySerializer()).isInstanceOf(RedisSerializer.class);
        assertThat(template.getValueSerializer()).isInstanceOf(RedisSerializer.class);
    }
}
