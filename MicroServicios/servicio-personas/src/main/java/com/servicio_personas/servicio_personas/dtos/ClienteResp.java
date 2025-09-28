package com.servicio_personas.servicio_personas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResp {
    private Long idCliente;
    private String nombre;
    private String identificacion;
    private Integer estado;
}