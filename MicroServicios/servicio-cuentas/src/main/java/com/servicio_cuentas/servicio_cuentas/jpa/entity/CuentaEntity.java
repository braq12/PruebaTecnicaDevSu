package com.servicio_cuentas.servicio_cuentas.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "CUENTA", schema = "SERVICIO_CUENTAS",
        uniqueConstraints = @UniqueConstraint(name = "UQ_CUENTA_NUMERO", columnNames = "NUMERO_CUENTA"))
public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuenta_seq")
    @SequenceGenerator(name = "cuenta_seq",
            sequenceName = "SERVICIO_CUENTAS.SEQ_CUENTA", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NUMERO_CUENTA", nullable = false, length = 30)
    private String numeroCuenta;

    @Column(name = "TIPO", nullable = false, length = 20) // AHORROS|CORRIENTE
    private String tipo;

    @Column(name = "SALDO_INICIAL", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "ESTADO", nullable = false)
    private Integer estado = 1; // 1=activa, 0=inactiva

    @Column(name = "ID_CLIENTE", nullable = false)
    private Long idCliente; // FK física a SERVICIO_PERSONAS.CLIENTE

    @Column(name = "FECHA_CREACION")
    private OffsetDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @PrePersist
    void prePersist() {
        fechaCreacion = OffsetDateTime.now();
        fechaActualizacion = fechaCreacion;
    }

    @PreUpdate
    void preUpdate() {
        fechaActualizacion = OffsetDateTime.now();
    }
}
