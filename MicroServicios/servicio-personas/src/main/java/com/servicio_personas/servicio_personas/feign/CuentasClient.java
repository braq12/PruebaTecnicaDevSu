package com.servicio_personas.servicio_personas.feign;


import com.servicio_personas.servicio_personas.dtos.CuentaCrearReq;
import com.servicio_personas.servicio_personas.dtos.CuentaResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "servicio-cuentas", url = "${cuentas.url}")
public interface CuentasClient {

    @PostMapping("/api/cuentas")
    CuentaResp crear(@RequestBody CuentaCrearReq req);
}
