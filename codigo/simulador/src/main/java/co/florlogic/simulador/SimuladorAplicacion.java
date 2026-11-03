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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootApplication (Spring Boot): marca la clase de arranque; Spring busca componentes desde este
 *   paquete hacia abajo y aplica la autoconfiguración. Sin servidor web porque application.yaml pone
 *   spring.main.web-application-type: none.
 * - CommandLineRunner: Spring llama a run(String... args) una vez, con los argumentos de la línea de órdenes,
 *   cuando el contexto ya está listo; aquí se los pasa a picocli.
 * - ExitCodeGenerator: Spring le pregunta getExitCode() al cerrar; así el código de picocli (0, 1, 2) llega al
 *   sistema operativo.
 * - new CommandLine(simulador, fabrica).execute(args) (picocli): interpreta los argumentos, elige la orden
 *   (sincronizar…) y la corre; devuelve el código de salida. La fábrica hace que picocli pida las órdenes a Spring.
 * - String... args: número variable de argumentos (varargs); dentro es un arreglo.
 * - System.exit(SpringApplication.exit(SpringApplication.run(...))): arranca Spring, al terminar lo cierra
 *   recogiendo el código de los ExitCodeGenerator, y termina el proceso con ese código.
 */
