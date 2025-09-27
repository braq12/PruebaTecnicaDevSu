package com.servicio_personas.servicio_personas.dtos;

import lombok.Data;

@Data
public class ActualizarClienteReq {
    // Persona
    private String nombre;
    private String genero;
    private Integer edad;
    private String direccion;
    private String telefono;

    // Cliente
    private Integer estado;     // 0|1 (opcional)
    private String contrasena;  // opcional
}