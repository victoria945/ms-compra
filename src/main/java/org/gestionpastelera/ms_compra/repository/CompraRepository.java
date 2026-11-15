package org.gestionpastelera.ms_compra.repository;

import org.gestionpastelera.ms_compra.enums.EstadoCompra;
import org.gestionpastelera.ms_compra.model.Compra;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Override
    @EntityGraph(attributePaths = "detalles")
    List<Compra> findAll();

    @Override
    @EntityGraph(attributePaths = "detalles")
    Optional<Compra> findById(Long id);

    @EntityGraph(attributePaths = "detalles")
    List<Compra> findByEstadoCompra(EstadoCompra estadoCompra);

    @EntityGraph(attributePaths = "detalles")
    List<Compra> findByProveedorRut(String proveedorRut);
}
