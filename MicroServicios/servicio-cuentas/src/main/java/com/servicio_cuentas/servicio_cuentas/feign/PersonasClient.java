package com.servicio_cuentas.servicio_cuentas.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "personas", url = "${personas.url}")
public interface PersonasClient {
    @GetMapping("/api/clientes/{id}")
    Map<String, Object> obtenerCliente(@PathVariable("id") Long id);
}