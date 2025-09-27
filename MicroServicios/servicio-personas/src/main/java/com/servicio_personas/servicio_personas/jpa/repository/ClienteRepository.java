package com.servicio_personas.servicio_personas.jpa.repository;

import com.servicio_personas.servicio_personas.jpa.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    @Query("""
            select c from ClienteEntity c
            join fetch c.persona p
            where c.idCliente = :id
            """)
    Optional<ClienteEntity> findDetalleById(Long id);

    @Query("""
            select c from ClienteEntity c
            join c.persona p
            where (:ident is null or p.identificacion = :ident)
              and (:nombre is null or lower(p.nombre) like lower(concat('%', :nombre, '%')))
            """)
    Page<ClienteEntity> buscar(String ident, String nombre, Pageable pageable);
}
