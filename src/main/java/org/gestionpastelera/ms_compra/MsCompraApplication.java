package org.gestionpastelera.ms_compra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCompraApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCompraApplication.class, args);
    }
}
