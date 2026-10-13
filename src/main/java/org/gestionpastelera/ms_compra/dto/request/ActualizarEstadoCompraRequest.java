package org.gestionpastelera.ms_compra.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.gestionpastelera.ms_compra.enums.EstadoCompra;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarEstadoCompraRequest {

    @NotNull(message = "El nuevo estado de la compra es obligatorio")
    private EstadoCompra estado;
}
