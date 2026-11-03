package co.florlogic.pruebas;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lo que hace el celular por el gateway (CT-1): abre una sesión y entrega la partición dorada en ella. Si abrir no da
 * {@code 201} (por ejemplo, {@code 502} con el backend apagado), devuelve esa respuesta. El token de operador llega
 * con la identidad (P3).
 */
final class EntregaDeLaDorada {

    static final String CELULAR = "0192f0a0-0000-7000-8000-0000000000e1";
    private static final Pattern SESION = Pattern.compile("\"sesion_uuid\"\\s*:\\s*\"([0-9a-f-]{36})\"");

    private EntregaDeLaDorada() {
    }

    static HttpResponse<String> entregar(HttpClient http, String base, Path dorada)
            throws IOException, InterruptedException {
        HttpResponse<String> abierta = post(http, URI.create(base + "/ingesta/v1/sesiones"), HttpRequest.BodyPublishers.ofString(
                "{\"contrato\":\"1.0\",\"dispositivo_uuid\":\"" + CELULAR + "\",\"reloj_ms\":" + System.currentTimeMillis()
                        + ",\"version_config\":12}"));
        if (abierta.statusCode() != 201) {
            return abierta;
        }
        Matcher id = SESION.matcher(abierta.body());
        if (!id.find()) {
            throw new IllegalStateException("La apertura no trajo sesion_uuid: " + abierta.body());
        }
        return post(http, URI.create(base + "/ingesta/v1/sesiones/" + id.group(1) + "/particiones"),
                HttpRequest.BodyPublishers.ofFile(dorada));
    }

    private static HttpResponse<String> post(HttpClient http, URI destino, HttpRequest.BodyPublisher cuerpo)
            throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(destino)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(cuerpo)
                .build(), HttpResponse.BodyHandlers.ofString());
    }
}
