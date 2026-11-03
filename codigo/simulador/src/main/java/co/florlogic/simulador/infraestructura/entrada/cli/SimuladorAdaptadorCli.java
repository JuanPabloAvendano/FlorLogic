package co.florlogic.simulador.infraestructura.entrada.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/** El celular simulado de TX-1 (TEC-25). Órdenes: {@code sincronizar} (camino mínimo, D-17). */
@Component
@Command(name = "simulador", mixinStandardHelpOptions = true, version = "0.1.0", subcommands = SincronizarAdaptadorCli.class,
        description = "Celular simulado: captura, almacén de salida y sincronización.")
public class SimuladorAdaptadorCli implements Runnable {

    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component (Spring): Spring crea este objeto; FabricaDeOrdenes se lo entrega a picocli.
 * - @Command(...) (picocli): declara la orden «simulador». mixinStandardHelpOptions = true agrega --help y
 *   --version; version = "0.1.0" es lo que imprime --version; subcommands registra «sincronizar»; description es
 *   el texto de la ayuda.
 * - implements Runnable / run(): lo que hace la orden si se llama sin suborden; aquí imprime la ayuda.
 * - @Spec CommandSpec spec (picocli): picocli inyecta la descripción de esta orden; spec.commandLine().usage(…)
 *   escribe la ayuda en la salida que tenga configurada (getOut()), que las pruebas cambian por un StringWriter.
 */
