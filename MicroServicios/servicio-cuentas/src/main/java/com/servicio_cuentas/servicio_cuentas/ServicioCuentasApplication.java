package com.servicio_cuentas.servicio_cuentas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServicioCuentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioCuentasApplication.class, args);
    }

}
