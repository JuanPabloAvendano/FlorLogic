package co.florlogic.simulador;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/** El celular simulado de TX-1 (TEC-25). Sin órdenes todavía. */
@Command(name = "simulador", mixinStandardHelpOptions = true, version = "0.1.0",
        description = "Celular simulado: captura, almacén de salida y sincronización.")
public class Simulador implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Simulador()).execute(args));
    }
}
