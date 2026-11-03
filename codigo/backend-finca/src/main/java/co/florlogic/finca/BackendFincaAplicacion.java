package co.florlogic.finca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendFincaAplicacion {

    public static void main(String[] args) {
        SpringApplication.run(BackendFincaAplicacion.class, args);
    }
}


/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootApplication (Spring Boot): junta tres cosas en una: esta clase es configuración, Spring Boot
 *   arma solo lo que encuentra en el classpath (base de datos, web, seguridad…) y busca componentes en este
 *   paquete co.florlogic.finca y todos sus subpaquetes. Por eso los diecisiete módulos cuelgan de aquí.
 * - SpringApplication.run(BackendFincaAplicacion.class, args): arranca el contenedor de Spring y el servidor web
 *   embebido; args son las opciones de línea de órdenes (por ejemplo, --spring.profiles.active=local).
 * - ModulosTest usa esta misma clase (ApplicationModules.of) como punto de partida para descubrir los módulos.
 */
