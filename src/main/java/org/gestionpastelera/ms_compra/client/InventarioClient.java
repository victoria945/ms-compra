package org.gestionpastelera.ms_compra.client;

import org.gestionpastelera.ms_compra.client.dto.RegistrarLoteClientRequest;
import org.gestionpastelera.ms_compra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventario", url = "${services.inventario.url}", configuration = FeignConfig.class)
public interface InventarioClient {

    @PostMapping("/api/v1/inventario")
    void registrarLote(@RequestBody RegistrarLoteClientRequest request);
}
