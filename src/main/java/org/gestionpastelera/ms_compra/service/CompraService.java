package org.gestionpastelera.ms_compra.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gestionpastelera.ms_compra.dto.request.CompraRequest;
import org.gestionpastelera.ms_compra.dto.request.DetalleCompraRequest;
import org.gestionpastelera.ms_compra.dto.response.CompraResponse;
import org.gestionpastelera.ms_compra.dto.response.DetalleCompraResponse;
import org.gestionpastelera.ms_compra.enums.EstadoCompra;
import org.gestionpastelera.ms_compra.mapper.CompraMapper;
import org.gestionpastelera.ms_compra.model.Compra;
import org.gestionpastelera.ms_compra.model.DetalleCompra;
import org.gestionpastelera.ms_compra.repository.CompraRepository;
import org.gestionpastelera.ms_compra.repository.DetalleCompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraService {

    private final CompraRepository compraRepository;
    private final CompraMapper compraMapper;
    private final DetalleCompraRepository detalleCompraRepository;

    @Transactional(readOnly = true)
    public List<CompraResponse> listarCompras(EstadoCompra estado) {
        List<Compra> compras;
        if (estado != null) {
            compras = compraRepository.findByEstadoCompra(estado);
        } else {
            compras = compraRepository.findAll();
        }
        return compraMapper.toResponseList(compras);
    }

    @Transactional(readOnly = true)
    public CompraResponse obtenerCompraPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden de compra con ID: " + id));
        return compraMapper.toResponse(compra);
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> obtenerComprasPorProveedor(String proveedorRut) {
        List<Compra> compras = compraRepository.findByProveedorRut(proveedorRut);
        return compraMapper.toResponseList(compras);
    }

    @Transactional
    public CompraResponse crearCompra(CompraRequest request, String usuarioRut) {
        BigDecimal costoTransporte = request.getCostoTransporte() != null
                ? request.getCostoTransporte()
                : BigDecimal.ZERO;

        BigDecimal totalProductos = BigDecimal.ZERO;
        List<DetalleCompra> detalles = new ArrayList<>();

        for (DetalleCompraRequest detReq : request.getDetalles()) {
            BigDecimal subtotal = detReq.getPrecioUnitario().multiply(BigDecimal.valueOf(detReq.getCantidad()));
            totalProductos = totalProductos.add(subtotal);

            DetalleCompra detalle = DetalleCompra.builder()
                    .codigoPastel(detReq.getCodigoPastel().trim())
                    .nombreProducto(detReq.getNombreProducto().trim())
                    .cantidad(detReq.getCantidad())
                    .precioUnitario(detReq.getPrecioUnitario())
                    .subtotal(subtotal)
                    .build();
            detalles.add(detalle);
        }

        BigDecimal totalCompra = totalProductos.add(costoTransporte);

        Compra compra = Compra.builder()
                .proveedorRut(request.getProveedorRut().trim())
                .proveedorNombre(request.getProveedorNombre().trim())
                .proveedorContacto(request.getProveedorContacto() != null ? request.getProveedorContacto().trim() : null)
                .fechaCompra(LocalDateTime.now())
                .costoTransporte(costoTransporte)
                .totalProductos(totalProductos)
                .totalCompra(totalCompra)
                .estadoCompra(EstadoCompra.PENDIENTE)
                .usuarioRut(usuarioRut)
                .notas(request.getNotas() != null ? request.getNotas().trim() : null)
                .build();

        for (DetalleCompra detalle : detalles) {
            compra.addDetalle(detalle);
        }

        Compra compraGuardada = compraRepository.save(compra);

        return compraMapper.toResponse(compraGuardada);
    }

    @Transactional
    public CompraResponse actualizarEstado(Long id, EstadoCompra nuevoEstado) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden de compra con ID: " + id));

        if (compra.getEstadoCompra() == nuevoEstado) {
            return compraMapper.toResponse(compra);
        }

        if (compra.getEstadoCompra() != EstadoCompra.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(
                    "No se permite cambiar el estado de una compra en estado %s a %s",
                    compra.getEstadoCompra(),
                    nuevoEstado
            ));
        }

        compra.setEstadoCompra(nuevoEstado);
        Compra compraActualizada = compraRepository.save(compra);

        return compraMapper.toResponse(compraActualizada);
    }

    @Transactional
    public void eliminarCompra(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden de compra con ID: " + id));

        if (compra.getEstadoCompra() == EstadoCompra.RECIBIDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar una orden de compra que ya fue RECIBIDA");
        }

        compraRepository.delete(compra);
    }

    @Transactional(readOnly = true)
    public List<DetalleCompraResponse> obtenerDetallesPorCompraId(Long compraId) {
        // Validar que la compra exista
        if (!compraRepository.existsById(compraId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden de compra con ID: " + compraId);
        }
        List<DetalleCompra> detalles = detalleCompraRepository.findByCompraId(compraId);
        return compraMapper.toDetalleResponseList(detalles);
    }
}
