package org.gestionpastelera.ms_compra.mapper;

import org.gestionpastelera.ms_compra.dto.request.DetalleCompraRequest;
import org.gestionpastelera.ms_compra.dto.response.CompraResponse;
import org.gestionpastelera.ms_compra.dto.response.DetalleCompraResponse;
import org.gestionpastelera.ms_compra.model.Compra;
import org.gestionpastelera.ms_compra.model.DetalleCompra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompraMapper {

    DetalleCompraResponse toDetalleResponse(DetalleCompra detalle);

    List<DetalleCompraResponse> toDetalleResponseList(List<DetalleCompra> detalles);

    @Mapping(target = "detalles", source = "detalles")
    CompraResponse toResponse(Compra compra);

    List<CompraResponse> toResponseList(List<Compra> compras);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compra", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    DetalleCompra toEntity(DetalleCompraRequest request);
}
