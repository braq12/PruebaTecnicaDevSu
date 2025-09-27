package com.servicio_cuentas.servicio_cuentas.dtos;
import lombok.AllArgsConstructor; import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data @AllArgsConstructor
public class MovimientoResp {
    private Long id;
    private Long idCuenta;
    private String tipo;
    private BigDecimal valor;
    private BigDecimal saldoDisponible;
    private OffsetDateTime fecha;
}
