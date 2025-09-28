package com.servicio_personas.servicio_personas.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CuentaCrearReq {
    @NotBlank private String numeroCuenta;
    @NotBlank private String tipo; // AHORROS|CORRIENTE
    @NotNull  private BigDecimal saldoInicial;
    @NotNull  private Long idCliente;
}
