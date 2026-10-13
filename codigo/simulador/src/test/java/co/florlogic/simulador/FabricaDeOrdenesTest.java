package co.florlogic.simulador;

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
        Simulador bean = new Simulador();
        when(contexto.getBean(Simulador.class)).thenReturn(bean);

        assertThat(fabrica.create(Simulador.class)).isSameAs(bean);
    }

    @Test
    void siNoEsBeanLoCreaPicocli() throws Exception {
        when(contexto.getBean(Simulador.class)).thenThrow(new NoSuchBeanDefinitionException(Simulador.class));

        assertThat(fabrica.create(Simulador.class)).isNotNull();
    }
}
