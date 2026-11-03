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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component (Spring): Spring crea un objeto de esta clase y lo inyecta donde se pida (SimuladorAplicacion).
 * - CommandLine.IFactory (picocli): el punto donde picocli pide «créame un objeto de esta clase»; por defecto
 *   usa reflexión, aquí se le pregunta primero a Spring.
 * - ApplicationContext.getBean(clase): pide a Spring el objeto ya creado de esa clase, con sus dependencias.
 * - NoSuchBeanDefinitionException: Spring no tiene esa clase; entonces la crea picocli con su fábrica de
 *   siempre (CommandLine.defaultFactory()), como pasa con sus clases de ayuda internas.
 * - <K> K create(Class<K> clase): método genérico; devuelve un objeto del mismo tipo que la clase pedida.
 */
