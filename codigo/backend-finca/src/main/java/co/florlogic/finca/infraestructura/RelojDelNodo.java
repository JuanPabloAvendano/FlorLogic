package co.florlogic.finca.infraestructura;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * El reloj del nodo de finca para todos los módulos, en UTC (ADR-031). Un bean y no {@code Instant.now()}: las pruebas
 * lo fijan, y ningún módulo depende de otro para saber la hora.
 */
@Configuration
public class RelojDelNodo {

    @Bean
    Clock reloj() {
        return Clock.systemUTC();
    }

    /**
     * La zona de la finca (F4): solo para decir qué jornada es «hoy» y en qué día cayó algo en la web. Lo guardado
     * sigue en UTC.
     */
    @Bean
    ZoneId zonaDeLaFinca(@Value("${florlogic.finca.zona:America/Bogota}") String zona) {
        return ZoneId.of(zona);
    }
}
