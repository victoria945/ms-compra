package org.gestionpastelera.ms_compra.dto.response;

import lombok.*;
import org.gestionpastelera.ms_compra.enums.EstadoCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraResponse {

    private Long id;
    private String usuarioRut;
    private String proveedorRut;
    private String proveedorNombre;
    private String proveedorContacto;
    private LocalDateTime fechaCompra;
    private BigDecimal costoTransporte;
    private BigDecimal totalProductos;
    private BigDecimal totalCompra;
    private EstadoCompra estadoCompra;
    private String notas;
    private List<DetalleCompraResponse> detalles;
}
