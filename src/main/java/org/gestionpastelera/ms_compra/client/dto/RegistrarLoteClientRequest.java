package org.gestionpastelera.ms_compra.client.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarLoteClientRequest {

    private String codigoPastel;
    private String nombrePastel;
    private Integer cantidad;
    private Long compraId;
    private LocalDate fechaVencimiento;
}
