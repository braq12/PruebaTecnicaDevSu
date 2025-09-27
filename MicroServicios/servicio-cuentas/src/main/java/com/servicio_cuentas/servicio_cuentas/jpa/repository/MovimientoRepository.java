package com.servicio_cuentas.servicio_cuentas.jpa.repository;
import com.servicio_cuentas.servicio_cuentas.jpa.entity.MovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoEntity, Long> {
    List<MovimientoEntity> findTop1ByCuentaIdOrderByFechaDescIdDesc(Long cuentaId);

    List<MovimientoEntity> findByCuentaIdAndFechaBetweenOrderByFechaAsc(Long cuentaId, OffsetDateTime desde, OffsetDateTime hasta);
}

