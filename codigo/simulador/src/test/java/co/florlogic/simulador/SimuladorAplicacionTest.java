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
