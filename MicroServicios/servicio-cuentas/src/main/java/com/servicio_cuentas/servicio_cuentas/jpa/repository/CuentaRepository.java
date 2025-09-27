package com.servicio_cuentas.servicio_cuentas.jpa.repository;

import com.servicio_cuentas.servicio_cuentas.jpa.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {
    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);
}
