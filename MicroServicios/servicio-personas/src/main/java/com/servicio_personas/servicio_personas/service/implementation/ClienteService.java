// service/implementation/ClienteService.java
package com.servicio_personas.servicio_personas.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.servicio_personas.servicio_personas.dtos.ActualizarClienteReq;
import com.servicio_personas.servicio_personas.dtos.ClienteResp;
import com.servicio_personas.servicio_personas.dtos.CrearClienteReq;
import com.servicio_personas.servicio_personas.jpa.entity.ClienteEntity;
import com.servicio_personas.servicio_personas.jpa.entity.PersonaEntity;
import com.servicio_personas.servicio_personas.jpa.repository.ClienteRepository;
import com.servicio_personas.servicio_personas.jpa.repository.PersonaRepository;
import com.servicio_personas.servicio_personas.mapper.ClienteMapper;
import com.servicio_personas.servicio_personas.mapper.PersonaMapper;
import com.servicio_personas.servicio_personas.messaging.EventosClienteProducer;
import com.servicio_personas.servicio_personas.service.IClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {

    private final PersonaRepository personaRepo;
    private final ClienteRepository clienteRepo;
    private final PersonaMapper personaMapper;
    private final ClienteMapper clienteMapper;
    private final EventosClienteProducer producer;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    @Transactional
    public ClienteResp crear(CrearClienteReq req) {
        personaRepo.findByIdentificacion(req.getIdentificacion())
                .ifPresent(p -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Identificación ya registrada");
                });

        PersonaEntity persona = personaMapper.toEntity(req);
        persona = personaRepo.save(persona);

        ClienteEntity cliente = new ClienteEntity();
        cliente.setPersona(persona);
        cliente.setContrasenaHash(hash(req.getContrasena()));
        cliente.setEstado(1);
        cliente = clienteRepo.save(cliente);

        publicarEvento("CustomerCreated", cliente);
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

        publicarEvento("CustomerUpdated", cliente);
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
        publicarEvento("CustomerDeactivated", cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long idCliente) {
        if (!clienteRepo.existsById(idCliente)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        clienteRepo.deleteById(idCliente);
        // (opcional) publicar un evento de borrado duro, si tu dominio lo requiere
    }

    private void publicarEvento(String type, ClienteEntity c) {
        try {
            ObjectNode payload = om.createObjectNode();
            payload.put("type", type);
            payload.put("eventId", UUID.randomUUID().toString());
            payload.put("occurredAt", OffsetDateTime.now().toString());
            ObjectNode data = payload.putObject("data");
            data.put("clienteId", c.getIdCliente());
            data.put("nombre", c.getPersona().getNombre());
            data.put("estado", c.getEstado());
            producer.publicar(om.writeValueAsString(payload));
        } catch (Exception ignored) {
        }
    }

    private String hash(String raw) {
        return Integer.toHexString(raw.hashCode()); // reemplazar por BCrypt en prod
    }
}
