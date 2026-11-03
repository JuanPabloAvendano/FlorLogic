package co.florlogic.simulador.infraestructura.salida.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
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
 * Adaptador de salida del celular: el nodo de finca por HTTPS (camino mínimo de TX-1, D-17; A-44).
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
    private final CredencialDelSimulador credencial;

    /**
     * El que usa Spring (decisión VS2-5 de la verificación del Segmento 2); el secreto, del entorno (CT-2), y la raíz de
     * la finca, de {@code FLORLOGIC_CA_RAIZ} (A-44).
     */
    NodoDeFincaAdaptadorHttp() {
        this(ConfianzaDeLaFinca.cliente());
    }

    /** El que usa la prueba, para elegir el cliente HTTP. */
    NodoDeFincaAdaptadorHttp(HttpClient http) {
        this(http, CredencialDelSimulador.delEntorno(http));
    }

    /** El que usa la prueba de la credencial. */
    NodoDeFincaAdaptadorHttp(HttpClient http, CredencialDelSimulador credencial) {
        this.http = http;
        this.credencial = credencial;
    }

    @Override
    public LecturaResultado<SesionAbiertaDto> abrir(URI nodo, UUID dispositivo, long relojMs, int versionConfig) {
        String cuerpo = "{\"contrato\":\"1.0\",\"dispositivo_uuid\":\"" + dispositivo + "\",\"reloj_ms\":" + relojMs
                + ",\"version_config\":" + versionConfig + "}";
        return pedir(nodo.resolve("/ingesta/v1/sesiones"), cuerpo.getBytes(), 201, SesionAbiertaDto.class);
    }

    @Override
    public LecturaResultado<SesionCerradaDto> cerrar(URI nodo, UUID sesion, boolean completa) {
        String cuerpo = "{\"contrato\":\"1.0\",\"completa\":" + completa + "}";
        return pedir(nodo.resolve("/ingesta/v1/sesiones/" + sesion + "/cierre"), cuerpo.getBytes(), 200, SesionCerradaDto.class);
    }

    @Override
    public LecturaResultado<ResultadoParticionDto> enviar(URI nodo, UUID sesion, byte[] particionJson) {
        Optional<HttpRequest.Builder> peticion = conCredencial(nodo,
                nodo.resolve("/ingesta/v1/sesiones/" + sesion + "/particiones"));
        if (peticion.isEmpty()) {
            return new LecturaResultado.Fallida<>("", "sin_credencial");
        }
        try {
            HttpResponse<byte[]> respuesta = http.send(peticion.get()
                    .timeout(ESPERA)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(particionJson))
                    .build(), HttpResponse.BodyHandlers.ofByteArray());
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

    /**
     * La petición con {@code Authorization: Bearer} si hay secreto (CT-2). Vacío si hay secreto y Keycloak no dio token:
     * mandar sin él sería un 401 seguro. Sin secreto, va sin token (el nodo dirá 401: así se ve en la prueba).
     */
    private Optional<HttpRequest.Builder> conCredencial(URI nodo, URI destino) {
        HttpRequest.Builder peticion;
        try {
            peticion = HttpRequest.newBuilder(destino);
        } catch (RuntimeException e) {
            return Optional.of(HttpRequest.newBuilder());   // URL mala: que falle abajo, donde ya se traduce
        }
        if (!credencial.configurada()) {
            return Optional.of(peticion);
        }
        return credencial.token(nodo).map(token -> peticion.header("Authorization", "Bearer " + token));
    }

    /** POST con el cuerpo dado; el estado esperado se lee con el lector tolerante (A-57). Nunca lanza. */
    private <T> LecturaResultado<T> pedir(URI destino, byte[] cuerpo, int esperado, Class<T> tipo) {
        Optional<HttpRequest.Builder> peticion = conCredencial(destino, destino);
        if (peticion.isEmpty()) {
            return new LecturaResultado.Fallida<>("", "sin_credencial");
        }
        try {
            HttpResponse<byte[]> respuesta = http.send(peticion.get()
                    .timeout(ESPERA)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo))
                    .build(), HttpResponse.BodyHandlers.ofByteArray());
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
