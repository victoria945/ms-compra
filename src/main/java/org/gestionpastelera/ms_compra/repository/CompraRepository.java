package org.gestionpastelera.ms_compra.repository;

import org.gestionpastelera.ms_compra.enums.EstadoCompra;
import org.gestionpastelera.ms_compra.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByEstadoCompra(EstadoCompra estadoCompra);

    List<Compra> findByProveedorRut(String proveedorRut);
}
