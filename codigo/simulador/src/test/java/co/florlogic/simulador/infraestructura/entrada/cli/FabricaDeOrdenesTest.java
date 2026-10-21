package co.florlogic.simulador.infraestructura.entrada.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

class FabricaDeOrdenesTest {

    private final ApplicationContext contexto = mock(ApplicationContext.class);
    private final FabricaDeOrdenes fabrica = new FabricaDeOrdenes(contexto);

    @Test
    void usaElBeanDeSpringSiExiste() throws Exception {
        SimuladorAdaptadorCli bean = new SimuladorAdaptadorCli();
        when(contexto.getBean(SimuladorAdaptadorCli.class)).thenReturn(bean);

        assertThat(fabrica.create(SimuladorAdaptadorCli.class)).isSameAs(bean);
    }

    @Test
    void siNoEsBeanLoCreaPicocli() throws Exception {
        when(contexto.getBean(SimuladorAdaptadorCli.class)).thenThrow(new NoSuchBeanDefinitionException(SimuladorAdaptadorCli.class));

        assertThat(fabrica.create(SimuladorAdaptadorCli.class)).isNotNull();
    }
}
