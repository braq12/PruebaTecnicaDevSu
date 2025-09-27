package com.servicio_personas.servicio_personas.jpa.repository;

import com.servicio_personas.servicio_personas.jpa.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {


    Optional<PersonaEntity> findByIdentificacion(String identificacion);
}
