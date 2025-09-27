package com.servicio_cuentas.servicio_cuentas.service;

import com.servicio_cuentas.servicio_cuentas.dtos.MovimientoCrearReq;
import com.servicio_cuentas.servicio_cuentas.dtos.MovimientoResp;

import java.time.OffsetDateTime;
import java.util.List;

public interface IMovimientoService {
    MovimientoResp registrar(MovimientoCrearReq req);
    List<MovimientoResp> listarPorRango(Long idCuenta, OffsetDateTime desde, OffsetDateTime hasta);
}
