package co.florlogic.finca.infraestructura;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * La PWA del celular servida por el nodo (F8, CT-4): {@code /app/} entrega la aplicación sin sesión, con la CSP
 * estricta, y nada que no sea leer pasa. Lo que la PWA hace después (iniciar sesión con Keycloak, entregar por
 * {@code /ingesta}) lo juzgan las cadenas de siempre. Se salta sin Docker.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class AppDelNodoTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    @Autowired
    WebApplicationContext contexto;

    MockMvc app;

    @BeforeEach
    void app() {
        app = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    }

    @Test
    void sin_barra_lleva_a_la_app_y_con_barra_entrega_el_index() throws Exception {
        app.perform(get("/app")).andExpect(status().isFound()).andExpect(redirectedUrl("/app/"));
        app.perform(get("/app/")).andExpect(status().isOk()).andExpect(forwardedUrl("/app/index.html"));
    }

    @Test
    void la_app_se_baja_sin_token_y_con_csp_estricta() throws Exception {
        app.perform(get("/app/index.html")).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("js/main.js")))
                .andExpect(header().string("Content-Security-Policy", Matchers.allOf(
                        Matchers.containsString("script-src 'self'"), Matchers.containsString("connect-src 'self'"),
                        Matchers.not(Matchers.containsString("unsafe-inline")))))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().doesNotExist("Set-Cookie"));
        app.perform(get("/app/js/main.js")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/javascript"));
        app.perform(get("/app/sw.js")).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("ARCHIVOS")));
        app.perform(get("/app/manifest.json")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string(Matchers.containsString("\"display\": \"standalone\"")));
    }

    @Test
    void en_la_app_solo_se_lee_y_lo_que_no_existe_es_404() throws Exception {
        app.perform(post("/app/")).andExpect(status().isForbidden());
        app.perform(post("/app/js/main.js")).andExpect(status().isForbidden());
        app.perform(get("/app/no-existe.js")).andExpect(status().isNotFound());
    }

    @Test
    void la_app_abierta_no_abre_la_api_ni_la_web() throws Exception {
        // Sin token, la ingesta sigue en 401 y la web manda a Keycloak: /app no cambia las otras dos cadenas.
        app.perform(post("/ingesta/v1/sesiones").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        app.perform(get("/web/tablero")).andExpect(status().isFound())
                .andExpect(redirectedUrl("/web/oauth2/authorization/keycloak"));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootTest, @Testcontainers(disabledWithoutDocker = true), @Container, @ServiceConnection: el contexto
 *   completo con PostgreSQL 16 en Docker (el backend no arranca sin base); sin Docker la clase se salta.
 * - MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()): peticiones simuladas sobre la aplicación
 *   real con los filtros de Spring Security puestos.
 * - status().isFound(): 302; redirectedUrl: a dónde manda. forwardedUrl: el reenvío interno (MockMvc no lo sigue,
 *   solo dice a dónde iría; el contenido de index.html se pide directo en la prueba siguiente).
 * - header().string("Content-Security-Policy", Matchers.allOf(…)): la cabecera trae script-src y connect-src propios
 *   y ningún 'unsafe-inline'. header().doesNotExist("Set-Cookie"): bajar la app no crea sesión.
 * - content().contentTypeCompatibleWith(…): el tipo de contenido; el manifiesto sale como application/json (por eso se
 *   llama manifest.json: Spring no conoce .webmanifest y lo daría como application/octet-stream).
 * - post(…) → isForbidden(): 403, la cadena de /app niega todo lo que no sea GET.
 * - isUnauthorized(): 401 de la API sin token.
 */
