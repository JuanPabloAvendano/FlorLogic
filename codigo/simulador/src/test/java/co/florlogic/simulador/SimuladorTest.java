package co.florlogic.simulador;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/** La línea de órdenes del simulador, sin Spring. */
class SimuladorTest {

    private final StringWriter salida = new StringWriter();
    private final StringWriter errores = new StringWriter();
    private CommandLine linea;

    @BeforeEach
    void preparar() {
        linea = new CommandLine(new Simulador());
        linea.setOut(new PrintWriter(salida));
        linea.setErr(new PrintWriter(errores));
    }

    @Test
    void sinArgumentosMuestraElUsoYTerminaBien() {
        assertThat(linea.execute()).isZero();
        assertThat(salida.toString())
                .contains("Usage: simulador")
                .contains("Celular simulado");
    }

    @Test
    void versionDevuelveLaDelSimulador() {
        assertThat(linea.execute("--version")).isZero();
        assertThat(salida.toString()).contains("0.1.0");
    }

    @Test
    void ayudaTerminaBien() {
        assertThat(linea.execute("--help")).isZero();
        assertThat(salida.toString()).contains("--version");
    }

    @Test
    void unaOpcionDesconocidaEsUnErrorDeUso() {
        assertThat(linea.execute("--no-existe")).isEqualTo(2);
        assertThat(errores.toString()).contains("--no-existe");
    }
}
