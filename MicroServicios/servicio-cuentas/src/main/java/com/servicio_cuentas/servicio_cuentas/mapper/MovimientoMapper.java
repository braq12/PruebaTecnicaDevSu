package com.servicio_cuentas.servicio_cuentas.mapper;

import com.servicio_cuentas.servicio_cuentas.dtos.MovimientoResp;
import com.servicio_cuentas.servicio_cuentas.jpa.entity.MovimientoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    @Mapping(target = "idCuenta", source = "cuenta.id")
    MovimientoResp toResp(MovimientoEntity e);
}
