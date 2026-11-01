package co.florlogic.finca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
// Los record de configuración que validan en su constructor (ESPEC-dominio-ingesta §5 «Configuración y arranque»).
@ConfigurationPropertiesScan
public class BackendFincaAplicacion {

    public static void main(String[] args) {
        SpringApplication.run(BackendFincaAplicacion.class, args);
    }
}

