package co.florlogic.enlinea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Recepción de respaldos, telemetría y panel (TX-2). */
@SpringBootApplication
public class ServiciosEnLineaAplicacion {

    public static void main(String[] args) {
        SpringApplication.run(ServiciosEnLineaAplicacion.class, args);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootApplication (Spring Boot): marca la clase de arranque; Spring busca componentes desde este
 *   paquete hacia abajo y aplica la autoconfiguración (aquí, servidor web y Actuator, por las dependencias del pom).
 * - SpringApplication.run(Clase.class, args): arranca el contexto de Spring y el servidor web en el puerto de
 *   application.yaml (8090).
 */
