package co.florlogic.simulador.infraestructura.salida.http;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * El token del simulador (CT-2): credenciales de cliente del cliente {@code simulador} de Keycloak, por el mismo
 * gateway que las particiones ({@code /auth/realms/florlogic/…/token}). Solo existe en desarrollo y CI
 * ({@code despliegue/keycloak/IA_crear-cliente-simulador.sh}); el celular de verdad entra con PKCE.
 *
 * <p>El secreto llega solo por la variable {@code FLORLOGIC_SIMULADOR_SECRETO}, nunca por la línea de órdenes (se
 * vería en {@code ps}). Sin la variable, el simulador pide sin token y el nodo responde 401. Guarda el token hasta 30 s
 * antes de que venza. Nunca lanza (A-57): si no consigue token, {@link #token} vuelve vacío.
 */
final class CredencialDelSimulador {

    static final String VARIABLE = "FLORLOGIC_SIMULADOR_SECRETO";
    static final String CLIENTE = "simulador";
    static final String RUTA_TOKEN = "/auth/realms/florlogic/protocol/openid-connect/token";

    private final HttpClient http;
    private final String secreto;
    private final Clock reloj;
    private String guardado;
    private Instant vence = Instant.MIN;

    /** El de Spring: el secreto de la variable de entorno. */
    static CredencialDelSimulador delEntorno(HttpClient http) {
        return new CredencialDelSimulador(http, System.getenv(VARIABLE), Clock.systemUTC());
    }

    CredencialDelSimulador(HttpClient http, String secreto, Clock reloj) {
        this.http = http;
        this.secreto = secreto == null || secreto.isBlank() ? null : secreto;
        this.reloj = reloj;
    }

    /** Si hay secreto: sin él, las peticiones van sin token (y el nodo dirá 401). */
    boolean configurada() {
        return secreto != null;
    }

    /** El token vigente para este nodo, o vacío si no hay secreto o Keycloak no lo dio. */
    synchronized Optional<String> token(URI nodo) {
        if (secreto == null) {
            return Optional.empty();
        }
        if (guardado != null && reloj.instant().isBefore(vence)) {
            return Optional.of(guardado);
        }
        try {
            String cuerpo = "grant_type=client_credentials&client_id=" + CLIENTE + "&client_secret="
                    + URLEncoder.encode(secreto, StandardCharsets.UTF_8);
            HttpResponse<String> respuesta = http.send(HttpRequest.newBuilder(nodo.resolve(RUTA_TOKEN))
                    .timeout(NodoDeFincaAdaptadorHttp.ESPERA)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                    .build(), HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                return Optional.empty();
            }
            JsonNode token = JsonMapper.builder().build().readTree(respuesta.body());
            if (!token.path("access_token").isString()) {
                return Optional.empty();
            }
            guardado = token.get("access_token").asString();
            vence = reloj.instant().plusSeconds(Math.max(0, token.path("expires_in").asLong(60) - 30));
            return Optional.of(guardado);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            // Sin red, JSON raro o URL mala: sin token, nunca una excepción hacia el celular (A-57).
            return Optional.empty();
        }
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - grant_type=client_credentials&client_id=…&client_secret=…: el flujo «credenciales de cliente» de OAuth 2.0
 *   (RFC 6749 secc. 4.4); un programa sin persona se identifica con su id y su secreto y recibe un token.
 * - "Content-Type", "application/x-www-form-urlencoded": el cuerpo va como formulario (clave=valor&…), que es lo
 *   que exige el punto /token de Keycloak.
 * - URLEncoder.encode(secreto, UTF_8): escapa el secreto para el formulario (un & dentro no parte los campos).
 * - nodo.resolve(RUTA_TOKEN): como la ruta empieza por barra, reemplaza la ruta del nodo y conserva esquema,
 *   máquina y puerto.
 * - HttpRequest.newBuilder(…).timeout(…).header(…).POST(BodyPublishers.ofString(cuerpo)).build() (JDK): arma la
 *   petición; http.send(..., BodyHandlers.ofString()) la manda y espera la respuesta como texto.
 * - access_token / expires_in: los miembros de la respuesta de Keycloak: el token y cuántos segundos dura.
 * - token.path("access_token") (Jackson 3): como get, pero si falta devuelve un MissingNode en vez de null.
 * - asLong(60): el número, o 60 si no es número.
 * - Clock (java.time): el reloj se recibe por constructor; Clock.systemUTC() en el programa, uno fijo en la prueba.
 * - synchronized: un solo hilo a la vez dentro de token(); el token guardado no se pide dos veces en paralelo.
 * - Instant.MIN: el instante más antiguo posible; al empezar, cualquier hora ya pasó el vencimiento.
 * - catch (InterruptedException e) + Thread.currentThread().interrupt(): si alguien interrumpe el hilo mientras
 *   espera la red, se vuelve a marcar la interrupción para que quien llama la vea.
 * - Optional<String>: el token o vacío; nunca null ni excepción hacia quien llama.
 */
