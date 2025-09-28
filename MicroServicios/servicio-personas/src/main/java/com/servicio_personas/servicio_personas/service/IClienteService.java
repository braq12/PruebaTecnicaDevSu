package com.servicio_personas.servicio_personas.service;

import com.servicio_personas.servicio_personas.dtos.ActualizarClienteReq;
import com.servicio_personas.servicio_personas.dtos.ClienteResp;
import com.servicio_personas.servicio_personas.dtos.CrearClienteReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IClienteService {

    ClienteResp crear(CrearClienteReq req);

    ClienteResp actualizar(Long idCliente, ActualizarClienteReq req);

    ClienteResp obtener(Long idCliente);

    Page<ClienteResp> listar(String identificacion, String nombre, Pageable pageable);

    void desactivar(Long idCliente);

    void eliminar(Long idCliente);

}
