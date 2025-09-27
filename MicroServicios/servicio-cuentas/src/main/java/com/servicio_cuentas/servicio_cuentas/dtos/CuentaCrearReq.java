package com.servicio_cuentas.servicio_cuentas.dtos;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CuentaCrearReq {
    @NotBlank private String numeroCuenta;
    @NotBlank private String tipo; // AHORROS|CORRIENTE
    @NotNull  private BigDecimal saldoInicial;
    @NotNull  private Long idCliente;
}
