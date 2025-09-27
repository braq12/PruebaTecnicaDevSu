package com.servicio_cuentas.servicio_cuentas.dtos;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovimientoCrearReq {
    @NotNull private Long idCuenta;
    @NotBlank private String tipo; // DEPOSITO|RETIRO
    @NotNull  private BigDecimal valor;
}

