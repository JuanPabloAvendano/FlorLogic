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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Configuration (Spring): clase que fabrica objetos para el contenedor; cada método @Bean entrega uno.
 * - @Bean Clock reloj(): el reloj compartido. Clock.systemUTC() da la hora del sistema en UTC; quien necesite la
 *   hora pide un Clock por constructor y las pruebas pueden darle Clock.fixed(…) en su lugar.
 * - @Bean ZoneId zonaDeLaFinca(…): ZoneId es una zona horaria con sus reglas (America/Bogota no tiene horario de
 *   verano). ZoneId.of falla al arrancar si el nombre no existe, así un error de configuración se ve de inmediato.
 * - @Value("${florlogic.finca.zona:America/Bogota}"): lee la clave florlogic.finca.zona de application.yaml (que a
 *   su vez la toma de la variable de entorno FLORLOGIC_FINCA_ZONA); lo que va después de los dos puntos es el valor
 *   por defecto si la clave no está.
 */
