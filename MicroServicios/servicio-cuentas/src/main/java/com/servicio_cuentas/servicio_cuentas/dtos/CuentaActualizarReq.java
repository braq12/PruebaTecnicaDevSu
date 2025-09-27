package com.servicio_cuentas.servicio_cuentas.dtos;
import lombok.Data;

@Data
public class CuentaActualizarReq {
    private String tipo;     // opcional
    private Integer estado;  // 0|1 opcional
}
