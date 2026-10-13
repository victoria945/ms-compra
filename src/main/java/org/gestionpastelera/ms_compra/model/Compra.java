package org.gestionpastelera.ms_compra.model;

import jakarta.persistence.*;
import lombok.*;
import org.gestionpastelera.ms_compra.enums.EstadoCompra;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proveedor_rut", nullable = false, length = 12)
    private String proveedorRut;

    @Column(name = "proveedor_nombre", nullable = false, length = 120)
    private String proveedorNombre;

    @Column(name = "proveedor_contacto", length = 120)
    private String proveedorContacto;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "costo_transporte", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoTransporte;

    @Column(name = "total_productos", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalProductos;

    @Column(name = "total_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCompra;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado_compra", nullable = false, length = 20)
    private EstadoCompra estadoCompra;

    @Column(name = "usuario_rut", nullable = false, length = 12)
    private String usuarioRut;

    @Column(length = 500)
    private String notas;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleCompra> detalles = new ArrayList<>();

    public void addDetalle(DetalleCompra detalle) {
        detalles.add(detalle);
        detalle.setCompra(this);
    }
}
