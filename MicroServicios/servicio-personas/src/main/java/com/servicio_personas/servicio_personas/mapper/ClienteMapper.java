package com.servicio_personas.servicio_personas.mapper;


import com.servicio_personas.servicio_personas.dtos.ClienteResp;
import com.servicio_personas.servicio_personas.jpa.entity.ClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "idCliente", source = "idCliente")
    @Mapping(target = "nombre", source = "persona.nombre")
    @Mapping(target = "identificacion", source = "persona.identificacion")
    @Mapping(target = "estado", source = "estado")
    ClienteResp toResp(ClienteEntity cliente);
}