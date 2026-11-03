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

    // Por defecto, el gateway de la finca, la única puerta del nodo de finca (ESPEC-gateway-finca A-8), solo por HTTPS
    // (A-44); la raíz de la finca, de FLORLOGIC_CA_RAIZ.
    @Option(names = "--url", defaultValue = "https://localhost:${env:FLORLOGIC_GATEWAY_PUERTO:-443}",
            description = "Gateway del nodo de finca (por defecto ${DEFAULT-VALUE}).")
    URI url;

    @Option(names = "--dispositivo", defaultValue = "0192f0a0-0000-7000-8000-0000000000e1",
            description = "Id del celular que se simula (UUID v7). La sesión de sincronización la abre el simulador.")
    UUID dispositivo;

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
        LecturaResultado<ResultadoParticionDto> resultado = sincronizar.ejecutar(new SincronizarParticionComando(url, dispositivo, json));
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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component (Spring) + @Command(name = "sincronizar", …) (picocli): la suborden es un objeto de Spring, así
 *   recibe su puerto por el constructor.
 * - implements Callable<Integer> / call(): picocli corre call() y usa el Integer devuelto como código de salida.
 * - @Spec CommandSpec spec: picocli inyecta la descripción de la orden; getOut() es su salida estándar.
 * - @Option(names = "--url", defaultValue = "https://localhost:${env:FLORLOGIC_GATEWAY_PUERTO:-443}"): opción
 *   de línea de órdenes. En defaultValue, picocli reemplaza la variable de entorno FLORLOGIC_GATEWAY_PUERTO y, si
 *   no existe, usa 443. En description, ${DEFAULT-VALUE} muestra ese valor en la ayuda.
 * - required = true en --archivo: sin él, picocli responde error de uso (código 2) antes de llamar a call().
 * - URI, UUID, Path: picocli convierte el texto de cada opción al tipo del campo.
 * - Files.readAllBytes(archivo): lee el archivo entero a bytes; IOException si no existe o no se puede leer.
 * - return switch (resultado) { case … -> { …; yield 0; } }: switch que devuelve un valor; yield es el valor de
 *   ese caso. case LecturaResultado.Leida<…> leida es un patrón de tipo: si coincide, deja la variable leida.
 * - case … fallo when fallo.clase().equals("rechazada"): patrón con guarda; solo aplica si la condición se cumple.
 *   El caso siguiente, sin guarda, recoge las demás Fallida.
 * - var conteo = …: Java infiere el tipo (ConteoDto).
 * - printf con %d y %n: número entero y salto de línea del sistema.
 */
