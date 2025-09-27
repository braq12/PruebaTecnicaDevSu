package com.servicio_cuentas.servicio_cuentas.service;


import com.servicio_cuentas.servicio_cuentas.dtos.CuentaActualizarReq;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaCrearReq;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaResp;

public interface ICuentaService {
    CuentaResp crear(CuentaCrearReq req);
    CuentaResp obtener(Long idCuenta);
    CuentaResp actualizar(Long idCuenta, CuentaActualizarReq req);
}
