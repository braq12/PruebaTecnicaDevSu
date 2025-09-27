package com.servicio_personas.servicio_personas.controller;

import com.servicio_personas.servicio_personas.dtos.ActualizarClienteReq;
import com.servicio_personas.servicio_personas.dtos.ClienteResp;
import com.servicio_personas.servicio_personas.dtos.CrearClienteReq;
import com.servicio_personas.servicio_personas.service.IClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService service;

    @PostMapping
    public ResponseEntity<ClienteResp> crear(@Valid @RequestBody CrearClienteReq req) {
        var resp = service.crear(req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResp> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResp>> listar(
            @RequestParam(required = false) String identificacion,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idCliente,desc") String sort) {

        String[] s = sort.split(",");
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(s.length > 1 ? s[1] : "desc"), s[0]));
        return ResponseEntity.ok(service.listar(identificacion, nombre, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResp> actualizar(@PathVariable Long id,
                                                  @RequestBody ActualizarClienteReq req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }

    @PatchMapping("/{id}/estado/{estado}")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @PathVariable Integer estado) {
        service.actualizar(id, patchEstado(estado));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // helper
    private ActualizarClienteReq patchEstado(Integer estado) {
        var dto = new ActualizarClienteReq();
        dto.setEstado(estado);
        return dto;
    }
}

