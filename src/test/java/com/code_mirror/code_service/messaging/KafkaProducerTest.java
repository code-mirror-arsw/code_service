package com.code_mirror.code_service.messaging;


import com.code_mirror.code_service.infrastructure.messaging.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class KafkaProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        kafkaProducer = new KafkaProducer();
        kafkaProducer.kafkaTemplate = kafkaTemplate;
    }

    @Test
    void sendMessage_sendsMessageToKafka() {
        String topic = "test-topic";
        String message = "Hello, Kafka!";

        kafkaProducer.sendMessage(topic, message);

        verify(kafkaTemplate, times(1)).send(topic, message);
    }
}
