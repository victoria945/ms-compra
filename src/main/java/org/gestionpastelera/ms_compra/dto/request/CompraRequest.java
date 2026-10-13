package org.gestionpastelera.ms_compra.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRequest {

    @NotBlank(message = "El RUT del proveedor es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9kK]$", message = "El RUT del proveedor debe tener el formato 12345678-9")
    private String proveedorRut;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String proveedorNombre;
    private String proveedorContacto;

    @DecimalMin(value = "0.0", message = "El costo de transporte no puede ser negativo")
    private BigDecimal costoTransporte;

    private String notas;

    @NotEmpty(message = "La compra debe tener al menos un detalle de producto")
    @Valid
    private List<DetalleCompraRequest> detalles;
}
