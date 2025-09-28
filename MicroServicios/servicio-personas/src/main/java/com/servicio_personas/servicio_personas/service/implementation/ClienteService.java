package com.servicio_personas.servicio_personas.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicio_personas.servicio_personas.dtos.ActualizarClienteReq;
import com.servicio_personas.servicio_personas.dtos.ClienteResp;
import com.servicio_personas.servicio_personas.dtos.CrearClienteReq;
import com.servicio_personas.servicio_personas.feign.CuentasClient;
import com.servicio_personas.servicio_personas.jpa.entity.ClienteEntity;
import com.servicio_personas.servicio_personas.jpa.repository.ClienteRepository;
import com.servicio_personas.servicio_personas.jpa.repository.PersonaRepository;
import com.servicio_personas.servicio_personas.mapper.ClienteMapper;
import com.servicio_personas.servicio_personas.mapper.PersonaMapper;
import com.servicio_personas.servicio_personas.service.IClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {

    private final PersonaRepository personaRepo;
    private final ClienteRepository clienteRepo;
    private final PersonaMapper personaMapper;
    private final ClienteMapper clienteMapper;
    private final ObjectMapper om = new ObjectMapper();
    private final CuentasClient cuentasClient;
    private final CuentaAutoService cuentaAutoService;

    @Override
    @Transactional
    public ClienteResp crear(CrearClienteReq req) {
        personaRepo.findByIdentificacion(req.getIdentificacion())
                .ifPresent(p -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Identificación ya registrada");
                });

        var persona = personaRepo.save(personaMapper.toEntity(req));

        var cliente = new ClienteEntity();
        cliente.setPersona(persona);
        cliente.setContrasenaHash(hash(req.getContrasena()));
        cliente.setEstado(1);
        cliente = clienteRepo.save(cliente);

        // Asíncrono: crear cuenta si el flag viene en true
        if (req.isCrearCuentaAutomatica()) {
            cuentaAutoService.crearCuentaInicialAsync(cliente.getIdCliente());
        }

        return clienteMapper.toResp(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResp obtener(Long idCliente) {
        var cli = clienteRepo.findDetalleById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        return clienteMapper.toResp(cli);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResp> listar(String identificacion, String nombre, Pageable pageable) {
        return clienteRepo.buscar(identificacion, nombre, pageable).map(clienteMapper::toResp);
    }

    @Override
    @Transactional
    public ClienteResp actualizar(Long idCliente, ActualizarClienteReq req) {
        ClienteEntity cliente = clienteRepo.findDetalleById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        // Patch sobre Persona
        personaMapper.updatePersonaFromDto(req, cliente.getPersona());

        // Patch sobre Cliente
        if (req.getContrasena() != null && !req.getContrasena().isBlank()) {
            cliente.setContrasenaHash(hash(req.getContrasena()));
        }
        if (req.getEstado() != null) {
            if (req.getEstado() != 0 && req.getEstado() != 1) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Estado inválido (0|1)");
            }
            cliente.setEstado(req.getEstado());
        }

        // Persistir (persona está en cascada si la entidad lo define; si no, guarda explícito)
        personaRepo.save(cliente.getPersona());
        cliente = clienteRepo.save(cliente);
        return clienteMapper.toResp(cliente);
    }

    @Override
    @Transactional
    public void desactivar(Long idCliente) {
        ClienteEntity cliente = clienteRepo.findById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        if (cliente.getEstado() == 0) return;
        cliente.setEstado(0);
        clienteRepo.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long idCliente) {
        if (!clienteRepo.existsById(idCliente)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        clienteRepo.deleteById(idCliente);
    }


    private String hash(String raw) {
        return Integer.toHexString(raw.hashCode());
    }
}
