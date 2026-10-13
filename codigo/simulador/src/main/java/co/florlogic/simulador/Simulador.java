package co.florlogic.simulador;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/** El celular simulado de TX-1 (TEC-25). Sin órdenes todavía. */
@Component
@Command(name = "simulador", mixinStandardHelpOptions = true, version = "0.1.0",
        description = "Celular simulado: captura, almacén de salida y sincronización.")
public class Simulador implements Runnable {

    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
