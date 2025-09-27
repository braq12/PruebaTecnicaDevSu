package com.servicio_personas.servicio_personas.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERSONA", schema = "SERVICIO_PERSONAS")
public class PersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persona_seq")
    @SequenceGenerator(name = "persona_seq",
            sequenceName = "SERVICIO_PERSONAS.SEQ_PERSONA",
            allocationSize = 1)
    @Column(name = "ID")
    private Long idPersona;

    @Column(name = "NOMBRE", nullable = false, length = 120)
    private String nombre;

    @Column(name = "GENERO", length = 20)
    private String genero;
    @Column(name = "EDAD")
    private Integer edad;

    @Column(name = "IDENTIFICACION", nullable = false, length = 50, unique = true)
    private String identificacion;

    @Column(name = "DIRECCION", length = 200)
    private String direccion;
    @Column(name = "TELEFONO", length = 30)
    private String telefono;
}
