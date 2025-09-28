package com.servicio_cuentas.servicio_cuentas.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaActualizarReq;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaCrearReq;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaResp;
import com.servicio_cuentas.servicio_cuentas.feign.PersonasClient;
import com.servicio_cuentas.servicio_cuentas.jpa.entity.CuentaEntity;
import com.servicio_cuentas.servicio_cuentas.jpa.repository.CuentaRepository;
import com.servicio_cuentas.servicio_cuentas.mapper.CuentaMapper;
import com.servicio_cuentas.servicio_cuentas.service.ICuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CuentaService implements ICuentaService {

    private final CuentaRepository cuentaRepo;
    private final CuentaMapper cuentaMapper;
    private final ObjectMapper om = new ObjectMapper();
    private final PersonasClient personasClient;

    @Override
    @Transactional
    public CuentaResp crear(CuentaCrearReq req) {

        var cli = validarClienteExisteYActivo(req.getIdCliente());
        cuentaRepo.findByNumeroCuenta(req.getNumeroCuenta())
                .ifPresent(x -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Número de cuenta ya existe");
                });

        if (!"AHORROS".equalsIgnoreCase(req.getTipo()) && !"CORRIENTE".equalsIgnoreCase(req.getTipo())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Tipo inválido (AHORROS|CORRIENTE)");
        }

        CuentaEntity cta = cuentaMapper.toEntity(req);
        cta.setTipo(req.getTipo().toUpperCase());
        cta.setEstado(1);
        cta = cuentaRepo.save(cta);

        return new CuentaResp(
                cta.getId(), cta.getNumeroCuenta(), cta.getTipo(), cta.getEstado(),
                cta.getIdCliente(), saldoActual(cta));
    }

    private Object validarClienteExisteYActivo(Long idCliente) {
        try {
            var resp = personasClient.obtenerCliente(idCliente);
            Object estado = resp.get("estado");
            if (estado instanceof Number num && num.intValue() == 0) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Cliente inactivo");
            }
            return resp;
        } catch (feign.FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cliente no existe");
        } catch (feign.FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No fue posible validar el cliente en servicio-personas");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResp obtener(Long idCuenta) {
        var c = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        return new CuentaResp(
                c.getId(), c.getNumeroCuenta(), c.getTipo(), c.getEstado(),
                c.getIdCliente(), saldoActual(c));
    }

    @Override
    @Transactional
    public CuentaResp actualizar(Long idCuenta, CuentaActualizarReq req) {
        var c = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

        if (req.getTipo() != null) {
            if (!"AHORROS".equalsIgnoreCase(req.getTipo()) && !"CORRIENTE".equalsIgnoreCase(req.getTipo()))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Tipo inválido (AHORROS|CORRIENTE)");
            c.setTipo(req.getTipo().toUpperCase());
        }
        if (req.getEstado() != null) {
            if (req.getEstado() != 0 && req.getEstado() != 1)
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Estado inválido (0|1)");
            c.setEstado(req.getEstado());
        }

        c = cuentaRepo.save(c);
        return new CuentaResp(
                c.getId(), c.getNumeroCuenta(), c.getTipo(), c.getEstado(),
                c.getIdCliente(), saldoActual(c));
    }


    private BigDecimal saldoActual(CuentaEntity c) {
        // Si no hay movimientos, el saldo actual es el saldo inicial
        return c.getSaldoInicial();
    }

}
