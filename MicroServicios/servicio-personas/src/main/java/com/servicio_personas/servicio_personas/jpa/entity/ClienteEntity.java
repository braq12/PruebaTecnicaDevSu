package com.servicio_personas.servicio_personas.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CLIENTE", schema = "SERVICIO_PERSONAS")
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persona_seq")
    @SequenceGenerator(name = "persona_seq",
            sequenceName = "SERVICIO_PERSONAS.SEQ_CLIENTE",
            allocationSize = 1)
    @Column(name = "ID_CLIENTE")
    private Long idCliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PERSONA")
    private PersonaEntity persona;

    @Column(name = "CONTRASENA_HASH", nullable = false, length = 200)
    private String contrasenaHash;

    /**
     * 1 = activo, 0 = inactivo
     */
    @Column(name = "ESTADO", nullable = false)
    private Integer estado = 1;
}

