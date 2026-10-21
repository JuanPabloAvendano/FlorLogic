package co.florlogic.simulador;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

import co.florlogic.simulador.infraestructura.entrada.cli.FabricaDeOrdenes;
import co.florlogic.simulador.infraestructura.entrada.cli.SimuladorAdaptadorCli;

/** Arranca Spring Boot sin servidor web y le pasa los argumentos a picocli. */
@SpringBootApplication
public class SimuladorAplicacion implements CommandLineRunner, ExitCodeGenerator {

    private final SimuladorAdaptadorCli simulador;
    private final FabricaDeOrdenes fabrica;
    private int codigoDeSalida;

    public SimuladorAplicacion(SimuladorAdaptadorCli simulador, FabricaDeOrdenes fabrica) {
        this.simulador = simulador;
        this.fabrica = fabrica;
    }

    @Override
    public void run(String... args) {
        codigoDeSalida = new CommandLine(simulador, fabrica).execute(args);
    }

    @Override
    public int getExitCode() {
        return codigoDeSalida;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SimuladorAplicacion.class, args)));
    }
}
