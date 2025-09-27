package com.servicio_cuentas.servicio_cuentas.mapper;


import com.servicio_cuentas.servicio_cuentas.dtos.CuentaActualizarReq;
import com.servicio_cuentas.servicio_cuentas.dtos.CuentaCrearReq;
import com.servicio_cuentas.servicio_cuentas.jpa.entity.CuentaEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CuentaMapper {
    @Mapping(target="id", ignore = true)
    CuentaEntity toEntity(CuentaCrearReq req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(@MappingTarget CuentaEntity cuenta, CuentaActualizarReq req);
}
