package co.florlogic.simulador;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import co.florlogic.simulador.infraestructura.entrada.cli.FabricaDeOrdenes;
import co.florlogic.simulador.infraestructura.entrada.cli.SimuladorAdaptadorCli;

/** Spring arranca sin servidor web y ya corrió la orden sin argumentos. */
@SpringBootTest
class SimuladorAplicacionTest {

    @Autowired
    ApplicationContext contexto;

    @Autowired
    SimuladorAplicacion aplicacion;

    @Test
    void arrancaSinServidorWeb() {
        assertThat(contexto.getBean(SimuladorAdaptadorCli.class)).isNotNull();
        assertThat(contexto.getBean(FabricaDeOrdenes.class)).isNotNull();
        assertThat(contexto.containsBean("tomcatServletWebServerFactory")).isFalse();
    }

    @Test
    void laOrdenSinArgumentosTerminaBien() {
        assertThat(aplicacion.getExitCode()).isZero();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootTest: la prueba arranca el contexto completo de Spring, como el programa; al arrancar corre el
 *   CommandLineRunner sin argumentos, por eso getExitCode() ya tiene valor.
 * - @Autowired: Spring inyecta en el campo el objeto que tiene (el contexto y la aplicación).
 * - @Test: cada caso de prueba.
 * - assertThat(...).isNotNull() / isFalse() / isZero() (AssertJ): las comprobaciones.
 * - containsBean("tomcatServletWebServerFactory"): ese objeto solo existe si Spring arrancó el servidor web
 *   Tomcat; que no esté prueba que el simulador corre sin servidor.
 */
