package co.florlogic.simulador.infraestructura.entrada.cli;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/** Deja que las órdenes de picocli sean beans de Spring, con sus dependencias inyectadas. */
@Component
public class FabricaDeOrdenes implements CommandLine.IFactory {

    private final ApplicationContext contexto;

    public FabricaDeOrdenes(ApplicationContext contexto) {
        this.contexto = contexto;
    }

    @Override
    public <K> K create(Class<K> clase) throws Exception {
        try {
            return contexto.getBean(clase);
        } catch (NoSuchBeanDefinitionException e) {
            return CommandLine.defaultFactory().create(clase);
        }
    }
}
