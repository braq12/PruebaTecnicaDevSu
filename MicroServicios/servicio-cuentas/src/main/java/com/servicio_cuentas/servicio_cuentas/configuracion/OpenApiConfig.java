package com.servicio_cuentas.servicio_cuentas.configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Servicio Cuentas - API")
                        .description("API REST del microservicio de Cuentas/Movimientos")
                        .version("v1.0"));


    }
}
