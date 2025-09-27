package com.servicio_personas.servicio_personas.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearClienteReq {
    @NotBlank
    private String nombre;
    private String genero;
    private Integer edad;
    @NotBlank
    private String identificacion;
    private String direccion;
    private String telefono;
    @NotBlank
    private String contrasena;
}
