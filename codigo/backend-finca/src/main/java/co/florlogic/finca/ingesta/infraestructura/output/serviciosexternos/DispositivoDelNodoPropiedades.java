package co.florlogic.finca.ingesta.infraestructura.output.serviciosexternos;

import java.util.Objects;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.DispositivoDelNodo;

/**
 * El celular que atiende este nodo, leído de {@code florlogic.ingesta.dispositivo-id} (ESPEC-dominio-ingesta §5
 * «Configuración y arranque», A-107). Valida en su constructor, sin {@code spring-boot-starter-validation} (no tiene
 * fila TEC): sin el valor, el backend no arranca.
 */
@ConfigurationProperties("florlogic.ingesta")
public record DispositivoDelNodoPropiedades(UUID dispositivoId) implements DispositivoDelNodo {

    public DispositivoDelNodoPropiedades {
        Objects.requireNonNull(dispositivoId,
                "Falta florlogic.ingesta.dispositivo-id (FLORLOGIC_INGESTA_DISPOSITIVO_ID): el nodo no sabe qué celular atiende");
    }
}
