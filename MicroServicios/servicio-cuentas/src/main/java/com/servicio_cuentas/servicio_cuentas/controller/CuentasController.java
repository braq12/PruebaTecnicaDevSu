package com.servicio_cuentas.servicio_cuentas.controller;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaActualizarReq;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaCrearReq;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaResp;
import com.servicio_cuentas.servicio_cuentas.service.ICuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentasController {

    private final ICuentaService service;

    /**
     *
     * @param req
     * @return
     */
    @PostMapping
    public ResponseEntity<CuentaResp> crear(@Valid @RequestBody CuentaCrearReq req) {
        return ResponseEntity.ok(service.crear(req));
    }

    /** Obtener cuenta por ID */
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResp> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    /** Actualizar cuenta (tipo/estado) */
    @PutMapping("/{id}")
    public ResponseEntity<CuentaResp> actualizar(@PathVariable Long id, @RequestBody CuentaActualizarReq req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }
}
