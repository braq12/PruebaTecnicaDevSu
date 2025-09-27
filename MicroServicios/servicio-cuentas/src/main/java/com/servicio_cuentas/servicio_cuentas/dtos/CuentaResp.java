package com.servicio_cuentas.servicio_cuentas.dtos;
import lombok.AllArgsConstructor; import lombok.Data;
import java.math.BigDecimal;

@Data @AllArgsConstructor
public class CuentaResp {
    private Long id;
    private String numeroCuenta;
    private String tipo;
    private Integer estado;
    private Long idCliente;
    private BigDecimal saldoActual; // calculado
}
