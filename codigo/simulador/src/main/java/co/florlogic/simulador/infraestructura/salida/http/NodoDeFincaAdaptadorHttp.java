package co.florlogic.simulador.infraestructura.salida.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.dto.SesionAbiertaDto;
import co.florlogic.contrato.dto.SesionCerradaDto;
import co.florlogic.contrato.lectura.LectorDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.simulador.servicios.puertos.salida.NodoDeFincaPuertoSalida;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adaptador de salida del celular: el nodo de finca por HTTP (camino mínimo de TX-1, D-17).
 *
 * <p>Nunca lanza (A-57): sin red, un 422 o una respuesta ilegible vuelven como {@link LecturaResultado.Fallida} con
 * una clase corta, para que quien llama decida. Reintentar no es parte del camino mínimo: es otra
 * funcionalidad (ESPEC §8, «después»).
 */
@Component
class NodoDeFincaAdaptadorHttp implements NodoDeFincaPuertoSalida {

    /** La espera del celular por una respuesta (ESPEC §3: 30 s). */
    static final Duration ESPERA = Duration.ofSeconds(30);

    private final HttpClient http;

    /** El que usa Spring (decisión VS2-5 de la verificación del Segmento 2). */
    NodoDeFincaAdaptadorHttp() {
        this(ConfianzaDeLaFinca.cliente());   // la raíz de la finca, de FLORLOGIC_CA_RAIZ (A-44)
    }

    /** El que usa la prueba, para elegir el cliente HTTP. */
    NodoDeFincaAdaptadorHttp(HttpClient http) {
        this.http = http;
    }

    @Override
    public LecturaResultado<SesionAbiertaDto> abrir(URI nodo, UUID dispositivo, long relojMs, int versionConfig) {
        String cuerpo = "{\"contrato\":\"1.0\",\"dispositivo_uuid\":\"" + dispositivo + "\",\"reloj_ms\":" + relojMs
                + ",\"version_config\":" + versionConfig + "}";
        return pedir(nodo, "/ingesta/v1/sesiones", cuerpo, 201, SesionAbiertaDto.class);
    }

    @Override
    public LecturaResultado<SesionCerradaDto> cerrar(URI nodo, UUID sesion, boolean completa) {
        String cuerpo = "{\"contrato\":\"1.0\",\"completa\":" + completa + "}";
        return pedir(nodo, "/ingesta/v1/sesiones/" + sesion + "/cierre", cuerpo, 200, SesionCerradaDto.class);
    }

    @Override
    public LecturaResultado<ResultadoParticionDto> enviar(URI nodo, UUID sesion, byte[] particionJson) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(nodo.resolve("/ingesta/v1/sesiones/" + sesion + "/particiones"))
                    .timeout(ESPERA)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(particionJson))
                    .build();
            HttpResponse<byte[]> respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofByteArray());
            return switch (respuesta.statusCode()) {
                case 200 -> LectorDto.tolerante().leer(JsonMapper.builder().build().readTree(respuesta.body()),
                        ResultadoParticionDto.class);
                case 422 -> new LecturaResultado.Fallida<>("", "rechazada");
                default -> new LecturaResultado.Fallida<>("", "http_" + respuesta.statusCode());
            };
        } catch (IOException e) {
            return new LecturaResultado.Fallida<>("", "sin_conexion");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new LecturaResultado.Fallida<>("", "interrumpida");
        } catch (RuntimeException e) {
            // URL mal escrita, cuerpo ilegible o un defecto propio: no debe tumbar al celular.
            return new LecturaResultado.Fallida<>("", "fallo_inesperado");
        }
    }

    /** POST de abrir o cerrar la sesión (CT-1): el estado esperado se lee con el lector tolerante (A-57). Nunca lanza. */
    private <T> LecturaResultado<T> pedir(URI nodo, String ruta, String cuerpo, int esperado, Class<T> tipo) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(nodo.resolve(ruta))
                    .timeout(ESPERA)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<byte[]> respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofByteArray());
            if (respuesta.statusCode() != esperado) {
                return new LecturaResultado.Fallida<>("", respuesta.statusCode() == 422 ? "rechazada" : "http_" + respuesta.statusCode());
            }
            return LectorDto.tolerante().leer(JsonMapper.builder().build().readTree(respuesta.body()), tipo);
        } catch (IOException e) {
            return new LecturaResultado.Fallida<>("", "sin_conexion");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new LecturaResultado.Fallida<>("", "interrumpida");
        } catch (RuntimeException e) {
            return new LecturaResultado.Fallida<>("", "fallo_inesperado");
        }
    }
}
