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
 * Lo que hace el celular por el gateway (CT-1): abre una sesión y entrega la partición dorada en ella, con el token de
 * operador que se le dé (CT-2). Si abrir no da {@code 201} (por ejemplo, {@code 502} con el backend apagado), devuelve
 * esa respuesta.
 */
final class EntregaDeLaDorada {

    static final String CELULAR = "0192f0a0-0000-7000-8000-0000000000e1";
    private static final Pattern SESION = Pattern.compile("\"sesion_uuid\"\\s*:\\s*\"([0-9a-f-]{36})\"");

    private EntregaDeLaDorada() {
    }

    static HttpResponse<String> entregar(HttpClient http, String base, Path dorada, String token)
            throws IOException, InterruptedException {
        HttpResponse<String> abierta = post(http, URI.create(base + "/ingesta/v1/sesiones"), token, HttpRequest.BodyPublishers.ofString(
                "{\"contrato\":\"1.0\",\"dispositivo_uuid\":\"" + CELULAR + "\",\"reloj_ms\":" + System.currentTimeMillis()
                        + ",\"version_config\":12}"));
        if (abierta.statusCode() != 201) {
            return abierta;
        }
        Matcher id = SESION.matcher(abierta.body());
        if (!id.find()) {
            throw new IllegalStateException("La apertura no trajo sesion_uuid: " + abierta.body());
        }
        return post(http, URI.create(base + "/ingesta/v1/sesiones/" + id.group(1) + "/particiones"), token,
                HttpRequest.BodyPublishers.ofFile(dorada));
    }

    /** Sin token ({@code null}) va sin Authorization: así se prueba el 401. */
    static HttpResponse<String> post(HttpClient http, URI destino, String token, HttpRequest.BodyPublisher cuerpo)
            throws IOException, InterruptedException {
        HttpRequest.Builder peticion = HttpRequest.newBuilder(destino);
        if (token != null) {
            peticion.header("Authorization", "Bearer " + token);
        }
        return http.send(peticion
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(cuerpo)
                .build(), HttpResponse.BodyHandlers.ofString());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Pattern.compile("\"sesion_uuid\"\\s*:\\s*\"([0-9a-f-]{36})\""): expresión regular que busca el miembro
 *   sesion_uuid en la respuesta; \\s* son espacios opcionales y el paréntesis captura los 36 caracteres del UUID.
 *   Va a mano: el pom de este módulo no declara Jackson ni el contrato.
 * - matcher(texto).find() / group(1): busca la primera coincidencia y devuelve lo capturado.
 * - HttpRequest.BodyPublishers.ofString(…) / ofFile(dorada) (JDK): el cuerpo de la petición desde texto o desde
 *   el archivo de la partición dorada, sin leerlo antes a memoria.
 * - header("Authorization", "Bearer " + token): el token de Keycloak; si es null, la cabecera no va (caso 401).
 * - header("Content-Type", "application/json"), timeout(Duration.ofSeconds(30)), POST(cuerpo): la petición.
 * - http.send(..., BodyHandlers.ofString()): manda y espera la respuesta como texto.
 * - throws IOException, InterruptedException: los errores de red se dejan pasar; la prueba que llama falla.
 */
