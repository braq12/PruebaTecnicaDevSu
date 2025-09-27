package com.servicio_personas.servicio_personas.configuracion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ConfiguracionErrores {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> rse(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("timestamp", Instant.now(), "message", Objects.requireNonNull(ex.getReason())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> beanValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now(),
                "message", "Validación falló",
                "detalles", ex.getBindingResult().getFieldErrors().stream()
                        .map(e -> e.getField() + ": " + e.getDefaultMessage()).toList()
        ));
    }
}
