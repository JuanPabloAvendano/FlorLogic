package co.florlogic.simulador.infraestructura.entrada.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionPuertoEntrada;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionComando;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

/**
 * {@code simulador sincronizar}: manda una partición ya armada (un archivo JSON) al nodo de finca.
 * Camino mínimo de TX-1 (D-17): todavía no arma la partición desde el almacén de salida ni reintenta.
 *
 * <p>Adaptador de entrada: lee las opciones, llama al puerto {@link SincronizarParticionPuertoEntrada} y traduce su
 * resultado a texto y a un código de salida. No sabe que detrás hay HTTP.
 *
 * <p>Código de salida: 0 recibida · 1 rechazada · 2 no se pudo (sin conexión, archivo, respuesta rara).
 */
@Component
@Command(name = "sincronizar", mixinStandardHelpOptions = true,
        description = "Envía una partición (archivo JSON) al nodo de finca y muestra el resultado.")
public class SincronizarAdaptadorCli implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    // Por defecto, el gateway de la finca, la puerta del nodo de finca (ESPEC-gateway-finca A-8); será la única cuando
    // el backend entre a Compose sin puerto publicado.
    @Option(names = "--url", defaultValue = "http://localhost:${env:FLORLOGIC_GATEWAY_PUERTO:-80}",
            description = "Gateway del nodo de finca (por defecto ${DEFAULT-VALUE}).")
    URI url;

    @Option(names = "--sesion", required = true, description = "Id de la sesión de sincronización (UUID).")
    UUID sesion;

    @Option(names = "--archivo", required = true, description = "Partición en JSON (ParticionDto).")
    Path archivo;

    private final SincronizarParticionPuertoEntrada sincronizar;

    public SincronizarAdaptadorCli(SincronizarParticionPuertoEntrada sincronizar) {
        this.sincronizar = sincronizar;
    }

    @Override
    public Integer call() {
        PrintWriter salida = spec.commandLine().getOut();
        byte[] json;
        try {
            json = Files.readAllBytes(archivo);
        } catch (IOException e) {
            salida.println("No se pudo leer " + archivo);
            return 2;
        }
        LecturaResultado<ResultadoParticionDto> resultado = sincronizar.ejecutar(new SincronizarParticionComando(url, sesion, json));
        return switch (resultado) {
            case LecturaResultado.Leida<ResultadoParticionDto> leida -> {
                var conteo = leida.valor().conteo();
                salida.printf("Recibida · %d anotaciones · %d nuevas · %d ya estaban · confirmado hasta %d%n",
                        conteo.recibidas(), conteo.aplicadas(), conteo.sinEfecto(), leida.valor().confirmadoHasta());
                yield 0;
            }
            case LecturaResultado.Fallida<ResultadoParticionDto> fallo when fallo.clase().equals("rechazada") -> {
                salida.println("Rechazada por el nodo de finca (la partición no tiene la forma del contrato).");
                yield 1;
            }
            case LecturaResultado.Fallida<ResultadoParticionDto> fallo -> {
                salida.println("No se pudo sincronizar: " + fallo.clase());
                yield 2;
            }
        };
    }
}
