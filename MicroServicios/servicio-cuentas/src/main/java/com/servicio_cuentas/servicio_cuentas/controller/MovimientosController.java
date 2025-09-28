package com.servicio_cuentas.servicio_cuentas.controller;


import com.servicio_cuentas.servicio_cuentas.dtos.MovimientoCrearReq;
import com.servicio_cuentas.servicio_cuentas.dtos.MovimientoResp;
import com.servicio_cuentas.servicio_cuentas.service.IMovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientosController {

    private final IMovimientoService service;

    /**
     * Registrar movimiento (DEPOSITO|RETIRO)
     * @param req
     * @return
     */
    @PostMapping
    public ResponseEntity<MovimientoResp> crear(@Valid @RequestBody MovimientoCrearReq req) {
        return ResponseEntity.ok(service.registrar(req));
    }

    /**
     * Listar movimientos por rango de fechas ISO-8601
     * @param idCuenta
     * @param desde
     * @param hasta
     * @return
     */
    @GetMapping
    public ResponseEntity<List<MovimientoResp>> listar(
            @RequestParam Long idCuenta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return ResponseEntity.ok(service.listarPorRango(idCuenta, desde, hasta));
    }
}
