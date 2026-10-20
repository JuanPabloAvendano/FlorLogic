package co.florlogic.simulador.infraestructura.salida.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.florlogic.contrato.dto.ResultadoParticion;
import co.florlogic.contrato.lectura.LectorDto;
import co.florlogic.contrato.lectura.Lectura;
import co.florlogic.simulador.servicios.puertos.salida.NodoDeFinca;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adaptador de salida del celular: el nodo de finca por HTTP (camino mínimo de TX-1, D-17).
 *
 * <p>Nunca lanza (A-57): sin red, un 422 o una respuesta ilegible vuelven como {@link Lectura.Fallida} con
 * una clase corta, para que quien llama decida. Reintentar no es parte del camino mínimo: es otra
 * funcionalidad (ESPEC §8, «después»).
 */
@Component
class ClienteIngesta implements NodoDeFinca {

    /** La espera del celular por una respuesta (ESPEC §3: 30 s). */
    static final Duration ESPERA = Duration.ofSeconds(30);

    private final HttpClient http;

    /** El que usa Spring (decisión VS2-5 de la verificación del Segmento 2). */
    ClienteIngesta() {
        this(HttpClient.newHttpClient());
    }

    /** El que usa la prueba, para elegir el cliente HTTP. */
    ClienteIngesta(HttpClient http) {
        this.http = http;
    }

    @Override
    public Lectura<ResultadoParticion> enviar(URI nodo, UUID sesion, byte[] particionJson) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(nodo.resolve("/ingesta/v1/sesiones/" + sesion + "/particiones"))
                    .timeout(ESPERA)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(particionJson))
                    .build();
            HttpResponse<byte[]> respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofByteArray());
            return switch (respuesta.statusCode()) {
                case 200 -> LectorDto.tolerante().leer(JsonMapper.builder().build().readTree(respuesta.body()),
                        ResultadoParticion.class);
                case 422 -> new Lectura.Fallida<>("", "rechazada");
                default -> new Lectura.Fallida<>("", "http_" + respuesta.statusCode());
            };
        } catch (IOException e) {
            return new Lectura.Fallida<>("", "sin_conexion");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Lectura.Fallida<>("", "interrumpida");
        } catch (RuntimeException e) {
            // URL mal escrita, cuerpo ilegible o un defecto propio: no debe tumbar al celular.
            return new Lectura.Fallida<>("", "fallo_inesperado");
        }
    }
}
