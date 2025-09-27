package com.servicio_personas.servicio_personas.messaging;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventosClienteProducer {

    private static final String TOPIC = "customer.events";
    private final KafkaTemplate<String, String> kafka;

    public void publicar(String json) {
        kafka.send(TOPIC, json);
    }
}
