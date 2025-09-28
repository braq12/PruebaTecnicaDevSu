package com.servicio_personas.servicio_personas.service.implementation;



import com.servicio_personas.servicio_personas.dtos.CuentaCrearReq;
import com.servicio_personas.servicio_personas.feign.CuentasClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaAutoService {

    private final CuentasClient cuentasClient;

    @Async("clienteExecutor")
    public void crearCuentaInicialAsync(Long idCliente) {
        try {
            String numero = "AH-" + idCliente + "-" + Instant.now().toEpochMilli();

            var req = CuentaCrearReq.builder()
                    .numeroCuenta(numero)
                    .tipo("AHORROS")
                    .saldoInicial(BigDecimal.ZERO)
                    .idCliente(idCliente)
                    .build();

            var resp = cuentasClient.crear(req);
            log.info("Cuenta automática creada para cliente {}: {}", idCliente, resp.getNumeroCuenta());
        } catch (Exception e) {
            log.warn("No se pudo crear la cuenta automática para cliente {}: {}", idCliente, e.getMessage());
        }
    }
}
