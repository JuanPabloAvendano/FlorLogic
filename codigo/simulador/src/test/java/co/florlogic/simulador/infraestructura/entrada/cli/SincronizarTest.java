package co.florlogic.simulador.infraestructura.entrada.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import co.florlogic.contrato.dto.Conteo;
import co.florlogic.contrato.dto.ResultadoParticion;
import co.florlogic.contrato.lectura.Lectura;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticion;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionComando;
import picocli.CommandLine;

/**
 * La orden {@code sincronizar} con un puerto falso (Test Stub): sin red ni Spring. Fija el código de salida, que
 * es el contrato de la orden con quien la llama (el script del canal lo usa): 0 recibida · 1 rechazada · 2 no se pudo.
 */
class SincronizarTest {

    private static final String SESION = "0192f0a1-7c00-7000-8000-00000000a001";

    @TempDir
    Path carpeta;

    private final StringWriter salida = new StringWriter();
    private final AtomicReference<SincronizarParticionComando> recibido = new AtomicReference<>();

    @Test
    void recibida_es_0_y_pasa_al_puerto_la_url_la_sesion_y_los_bytes() throws IOException {
        Path archivo = Files.writeString(carpeta.resolve("p.json"), "{\"contrato\":\"1.0\"}");

        int codigo = correr(new Lectura.Leida<>(resultado(2, 1, 1)),
                "--url", "http://finca:18080", "--sesion", SESION, "--archivo", archivo.toString());

        assertThat(codigo).isZero();
        assertThat(salida.toString()).contains("2 anotaciones · 1 nuevas · 1 ya estaban");
        assertThat(recibido.get().nodo()).isEqualTo(URI.create("http://finca:18080"));
        assertThat(recibido.get().sesion()).isEqualTo(UUID.fromString(SESION));
        assertThat(recibido.get().particionJson()).isEqualTo(Files.readAllBytes(archivo));
    }

    @Test
    void rechazada_es_1() throws IOException {
        Path archivo = Files.writeString(carpeta.resolve("p.json"), "{}");

        assertThat(correr(new Lectura.Fallida<>("", "rechazada"), "--sesion", SESION, "--archivo", archivo.toString()))
                .isEqualTo(1);
        assertThat(salida.toString()).contains("Rechazada");
    }

    @Test
    void cualquier_otro_fallo_es_2_y_dice_su_clase() throws IOException {
        Path archivo = Files.writeString(carpeta.resolve("p.json"), "{}");

        assertThat(correr(new Lectura.Fallida<>("", "sin_conexion"), "--sesion", SESION, "--archivo", archivo.toString()))
                .isEqualTo(2);
        assertThat(salida.toString()).contains("sin_conexion");
    }

    @Test
    void un_archivo_que_no_existe_es_2_y_no_llama_al_puerto() {
        assertThat(correr(new Lectura.Leida<>(resultado(0, 0, 0)),
                "--sesion", SESION, "--archivo", carpeta.resolve("no-existe.json").toString()))
                .isEqualTo(2);
        assertThat(recibido.get()).isNull();
    }

    private int correr(Lectura<ResultadoParticion> respuesta, String... argumentos) {
        SincronizarParticion puerto = comando -> {
            recibido.set(comando);
            return respuesta;
        };
        CommandLine linea = new CommandLine(new Sincronizar(puerto));
        linea.setOut(new PrintWriter(salida, true));
        return linea.execute(argumentos);
    }

    private static ResultadoParticion resultado(int recibidas, int aplicadas, int sinEfecto) {
        return new ResultadoParticion(UUID.fromString("0192f0a1-7d10-7000-8000-00000000b001"), 2, 0, 0,
                new Conteo(recibidas, aplicadas, 0, 0, sinEfecto), List.of(), null, null);
    }
}
