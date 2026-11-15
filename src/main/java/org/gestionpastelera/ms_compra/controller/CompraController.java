package org.gestionpastelera.ms_compra.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestionpastelera.ms_compra.dto.request.ActualizarEstadoCompraRequest;
import org.gestionpastelera.ms_compra.dto.request.CompraRequest;
import org.gestionpastelera.ms_compra.dto.response.CompraResponse;
import org.gestionpastelera.ms_compra.enums.EstadoCompra;
import org.gestionpastelera.ms_compra.service.CompraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR_SUPERVISOR')")
    public ResponseEntity<List<CompraResponse>> listarCompras(
            @RequestParam(required = false) EstadoCompra estado
    ) {
        return ResponseEntity.ok(compraService.listarCompras(estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR_SUPERVISOR')")
    public ResponseEntity<CompraResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerCompraPorId(id));
    }

    @GetMapping("/proveedor/{rut}")
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR_SUPERVISOR')")
    public ResponseEntity<List<CompraResponse>> obtenerPorProveedor(@PathVariable String rut) {
        return ResponseEntity.ok(compraService.obtenerComprasPorProveedor(rut));
    }

    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR_SUPERVISOR')")
    public ResponseEntity<List<org.gestionpastelera.ms_compra.dto.response.DetalleCompraResponse>> obtenerDetalles(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerDetallesPorCompraId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR_SUPERVISOR')")
    public ResponseEntity<CompraResponse> crearCompra(
            @Valid @RequestBody CompraRequest request,
            Authentication authentication
    ) {
        String usuarioRut = authentication != null ? authentication.getName() : "SISTEMA";
        CompraResponse response = compraService.crearCompra(request, usuarioRut);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR_SUPERVISOR')")
    public ResponseEntity<CompraResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoCompraRequest request
    ) {
        return ResponseEntity.ok(compraService.actualizarEstado(id, request.getEstado()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCompra(@PathVariable Long id) {
        compraService.eliminarCompra(id);
        return ResponseEntity.noContent().build();
    }
}
