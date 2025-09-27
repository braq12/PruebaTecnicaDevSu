package com.servicio_cuentas.servicio_cuentas.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "MOVIMIENTO", schema = "SERVICIO_CUENTAS")
public class MovimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mov_seq")
    @SequenceGenerator(name = "mov_seq",
            sequenceName = "SERVICIO_CUENTAS.SEQ_MOVIMIENTO", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CUENTA")
    private CuentaEntity cuenta;

    @Column(name = "FECHA", nullable = false)
    private OffsetDateTime fecha = OffsetDateTime.now();

    @Column(name = "TIPO", nullable = false, length = 20) // DEPOSITO|RETIRO
    private String tipo;

    @Column(name = "VALOR", nullable = false, precision = 18, scale = 2)
    private BigDecimal valor;

    @Column(name = "SALDO_DISPONIBLE", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoDisponible;

    @Column(name = "FECHA_CREACION", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();
}
