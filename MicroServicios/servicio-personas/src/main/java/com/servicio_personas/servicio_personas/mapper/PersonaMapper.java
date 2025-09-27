package com.servicio_personas.servicio_personas.mapper;


import com.servicio_personas.servicio_personas.dtos.ActualizarClienteReq;
import com.servicio_personas.servicio_personas.dtos.CrearClienteReq;
import com.servicio_personas.servicio_personas.jpa.entity.PersonaEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PersonaMapper {

    @Mapping(target = "idPersona", ignore = true)
    PersonaEntity toEntity(CrearClienteReq req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonaFromDto(ActualizarClienteReq dto, @MappingTarget PersonaEntity persona);

}