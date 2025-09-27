// src/main/java/com/servicio_cuentas/servicio_cuentas/service/implementation/MovimientoService.java
package com.servicio_cuentas.servicio_cuentas.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.servicio_cuentas.servicio_cuentas.dtos.MovimientoCrearReq;
import com.servicio_cuentas.servicio_cuentas.dtos.MovimientoResp;
import com.servicio_cuentas.servicio_cuentas.jpa.entity.CuentaEntity;
import com.servicio_cuentas.servicio_cuentas.jpa.entity.MovimientoEntity;
import com.servicio_cuentas.servicio_cuentas.jpa.repository.CuentaRepository;
import com.servicio_cuentas.servicio_cuentas.jpa.repository.MovimientoRepository;
import com.servicio_cuentas.servicio_cuentas.mapper.MovimientoMapper;
import com.servicio_cuentas.servicio_cuentas.messaging.EventosCuentasProducer;
import com.servicio_cuentas.servicio_cuentas.service.IMovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovimientoService implements IMovimientoService {

    private final CuentaRepository cuentaRepo;
    private final MovimientoRepository movRepo;
    private final MovimientoMapper movMapper;
    private final EventosCuentasProducer producer;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    @Transactional
    public MovimientoResp registrar(MovimientoCrearReq req) {
        CuentaEntity c = cuentaRepo.findById(req.getIdCuenta())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        if (c.getEstado() == 0)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cuenta inactiva");

        BigDecimal saldoAnterior = saldoActual(c.getId());
        BigDecimal nuevoSaldo;

        String tipo = req.getTipo().toUpperCase();
        switch (tipo) {
            case "DEPOSITO" -> nuevoSaldo = saldoAnterior.add(req.getValor());
            case "RETIRO" -> {
                nuevoSaldo = saldoAnterior.subtract(req.getValor());
                if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0)
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Saldo no disponible");
            }
            default ->
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Tipo inválido (DEPOSITO|RETIRO)");
        }

        MovimientoEntity m = new MovimientoEntity();
        m.setCuenta(c);
        m.setTipo(tipo);
        m.setValor(req.getValor());
        m.setSaldoDisponible(nuevoSaldo);
        m = movRepo.save(m);

        publicarMovimientoRegistrado(m);
        return movMapper.toResp(m);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResp> listarPorRango(Long idCuenta, OffsetDateTime desde, OffsetDateTime hasta) {
        var lista = movRepo.findByCuentaIdAndFechaBetweenOrderByFechaAsc(idCuenta, desde, hasta);
        return lista.stream().map(movMapper::toResp).toList();
    }

    /* ================== helpers ================== */

    private BigDecimal saldoActual(Long idCuenta) {
        var ultimos = movRepo.findTop1ByCuentaIdOrderByFechaDescIdDesc(idCuenta);
        if (!ultimos.isEmpty()) return ultimos.getFirst().getSaldoDisponible();

        var c = cuentaRepo.findById(idCuenta).orElseThrow();  // sin movimientos -> saldo inicial
        return c.getSaldoInicial();
    }

    private void publicarMovimientoRegistrado(MovimientoEntity m) {
        try {
            ObjectNode payload = om.createObjectNode();
            payload.put("type", "MovementRegistered");
            payload.put("eventId", UUID.randomUUID().toString());
            payload.put("occurredAt", OffsetDateTime.now().toString());
            var data = payload.putObject("data");
            data.put("movimientoId", m.getId());
            data.put("cuentaId", m.getCuenta().getId());
            data.put("tipo", m.getTipo());
            data.put("valor", m.getValor());
            data.put("saldoDisponible", m.getSaldoDisponible());
            producer.publicarMovimientoRegistrado(om.writeValueAsString(payload));
        } catch (Exception ignored) {
        }
    }
}
