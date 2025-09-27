package com.servicio_cuentas.servicio_cuentas.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventosCuentasProducer {
    private final KafkaTemplate<String, String> kafka;

    public void publicarCuentaCreada(String json) {
        kafka.send("account.events", json);
    }

    public void publicarMovimientoRegistrado(String json) {
        kafka.send("movement.events", json);
    }
}
