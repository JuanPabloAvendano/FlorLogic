package co.florlogic.finca.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import co.florlogic.finca.bitacora.Bitacora;
import co.florlogic.finca.dispositivos.EstadoDeDispositivo;
import co.florlogic.finca.dispositivos.RegistroDeDispositivos;

/**
 * La web del gerente y del administrador de punta a punta contra PostgreSQL (F4, CT-2, CT-10, CT-13): quién entra a
 * qué, CSRF, aprobar y bloquear, asignar, el tablero y la cola del gerente, y lo que queda en la bitácora. La sesión de
 * Keycloak la simula {@code oidcLogin()} con los roles ya puestos; cómo salen del token de acceso lo prueba
 * {@link PersonasDeLaWebTest}, y el inicio de sesión de verdad (contraseña y TOTP), las de extremo a extremo. Se salta
 * sin Docker.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class WebDelNodoTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    @Autowired
    WebApplicationContext contexto;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    RegistroDeDispositivos dispositivos;

    @Autowired
    Bitacora bitacora;

    MockMvc web;

    @BeforeEach
    void web() {
        web = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    }

    private static OidcLoginRequestPostProcessor como(String sub, String rol) {
        return oidcLogin().idToken(t -> t.subject(sub).claim("preferred_username", sub))
                .authorities(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    private static final OidcLoginRequestPostProcessor GERENTE = como("gerente-web", "gerente");
    private static final OidcLoginRequestPostProcessor ADMINISTRADOR = como("admin-web", "administrador");

    @Test
    void sin_sesion_la_web_manda_a_keycloak_con_pkce() throws Exception {
        web.perform(get("/web/tablero")).andExpect(status().isFound())
                .andExpect(redirectedUrl("/web/oauth2/authorization/keycloak"));

        MvcResult r = web.perform(get("/web/oauth2/authorization/keycloak")).andExpect(status().isFound()).andReturn();
        String keycloak = r.getResponse().getRedirectedUrl();
        assertThat(keycloak).startsWith("http://localhost/auth/realms/florlogic/protocol/openid-connect/auth?")
                .contains("client_id=web", "code_challenge_method=S256", "scope=openid%20profile",
                        "redirect_uri=http://localhost/web/login/oauth2/code/keycloak");
    }

    @Test
    void un_operador_no_entra_a_la_web_y_una_ruta_sin_regla_no_existe() throws Exception {
        web.perform(get("/web/tablero").with(como("op-web", "operador"))).andExpect(status().isForbidden());
        web.perform(get("/web/otra-cosa").with(ADMINISTRADOR)).andExpect(status().isForbidden());
    }

    @Test
    void la_web_lleva_csp_estricta_y_htmx_propio() throws Exception {
        web.perform(get("/web/tablero").with(GERENTE)).andExpect(status().isOk())
                .andExpect(header().string("Content-Security-Policy", Matchers.containsString("script-src 'self'")))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(content().string(Matchers.containsString("/web/estatico/htmx-2.0.11.min.js")));
        web.perform(get("/web/estatico/htmx-2.0.11.min.js")).andExpect(status().isOk());
    }

    @Test
    void el_gerente_no_administra_celulares_ni_el_administrador_resuelve_trabajos() throws Exception {
        UUID celular = UUID.fromString("0192f0a0-0000-7000-8000-0000000000f1");
        dispositivos.reconocer(celular);

        web.perform(get("/web/dispositivos").with(GERENTE)).andExpect(status().isForbidden());
        web.perform(post("/web/dispositivos/" + celular + "/aprobar").with(GERENTE).with(csrf()))
                .andExpect(status().isForbidden());
        web.perform(get("/web/trabajos").with(ADMINISTRADOR)).andExpect(status().isForbidden());
        assertThat(estado(celular)).isEqualTo(EstadoDeDispositivo.PENDIENTE);
    }

    @Test
    void el_administrador_aprueba_con_htmx_y_queda_en_la_bitacora() throws Exception {
        UUID celular = UUID.fromString("0192f0a0-0000-7000-8000-0000000000f2");
        dispositivos.reconocer(celular);

        web.perform(get("/web/dispositivos").with(ADMINISTRADOR)).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(celular.toString())));
        web.perform(post("/web/dispositivos/" + celular + "/aprobar").with(ADMINISTRADOR).with(csrf())
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.allOf(Matchers.containsString("APROBADO"),
                        Matchers.containsString("admin-web"), Matchers.not(Matchers.containsString("<html")))));

        assertThat(estado(celular)).isEqualTo(EstadoDeDispositivo.APROBADO);
        assertThat(jdbc.queryForList("SELECT actor FROM bitacora WHERE evento = 'dispositivo_aprobado' AND objeto = ?",
                String.class, celular.toString())).containsExactly("admin-web");
        assertThat(bitacora.cadenaIntegra()).isTrue();

        // Sin htmx, el formulario de siempre: vuelve a la lista.
        web.perform(post("/web/dispositivos/" + celular + "/bloquear").with(ADMINISTRADOR).with(csrf()))
                .andExpect(status().isFound()).andExpect(redirectedUrl("/web/dispositivos"));
        assertThat(estado(celular)).isEqualTo(EstadoDeDispositivo.BLOQUEADO);
    }

    @Test
    void sin_csrf_no_cambia_nada() throws Exception {
        UUID celular = UUID.fromString("0192f0a0-0000-7000-8000-0000000000f3");
        dispositivos.reconocer(celular);

        web.perform(post("/web/dispositivos/" + celular + "/aprobar").with(ADMINISTRADOR).header("HX-Request", "true"))
                .andExpect(status().isForbidden());
        assertThat(estado(celular)).isEqualTo(EstadoDeDispositivo.PENDIENTE);
    }

    @Test
    void asignar_a_un_celular_pendiente_responde_422_con_el_motivo() throws Exception {
        UUID celular = UUID.fromString("0192f0a0-0000-7000-8000-0000000000f4");
        dispositivos.reconocer(celular);

        web.perform(post("/web/asignacion").with(ADMINISTRADOR).with(csrf()).param("jornada", "2026-03-14")
                        .param("dispositivo", celular.toString()).param("secciones", "B1").param("campos", "tallos"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string(Matchers.containsString("Solo un celular aprobado")));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM asignacion WHERE dispositivo_id = ?", Integer.class, celular))
                .isZero();
    }

    @Test
    void el_tablero_cruza_lo_asignado_con_lo_recibido() throws Exception {
        LocalDate jornada = LocalDate.parse("2026-03-15");
        UUID celular = UUID.fromString("0192f0a0-0000-7000-8000-0000000000f5");
        dispositivos.aprobar(celular, "admin-web");

        // 2 secciones × 2 campos = 4 celdas esperadas.
        web.perform(post("/web/asignacion").with(ADMINISTRADOR).with(csrf()).param("jornada", jornada.toString())
                        .param("dispositivo", celular.toString()).param("secciones", "T1-S1, T1-S2")
                        .param("campos", "tallos\nbotones"))
                .andExpect(status().isFound()).andExpect(redirectedUrl("/web/asignacion?jornada=2026-03-15"));
        web.perform(get("/web/asignacion").param("jornada", jornada.toString()).with(ADMINISTRADOR))
                .andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("T1-S1, T1-S2")));
        assertThat(jdbc.queryForList("SELECT evento FROM bitacora WHERE objeto = ?", String.class, celular.toString()))
                .contains("asignacion_cambiada");

        // Llega una celda (la capturó otro celular: igual cuenta) y una marcada; una rechazada en una sesión parcial.
        jdbc.update("""
                INSERT INTO hecho_produccion (produccion_id, seccion_id, campo_id, jornada, valor, capturado_en,
                    anotacion_uuid, dispositivo_id)
                VALUES ('P1', 'T1-S1', 'tallos', ?, '12', now(), gen_random_uuid(), gen_random_uuid())""", jornada);
        jdbc.update("""
                INSERT INTO trabajo_gerente (anotacion_uuid, dispositivo_id, seccion_id, jornada, codigos, abierto_en)
                VALUES (gen_random_uuid(), ?, 'T1-S2', ?, ARRAY['incompleta'], now())""", celular, jornada);
        UUID sesion = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO sesion_sincronizacion (sesion_id, dispositivo_id, persona, abierta_en, desfase_ms,
                    version_config, dispositivo_registrado, estado, cerrada_en)
                VALUES (?, ?, 'op', TIMESTAMPTZ '2026-03-15 12:00:00-05', 0, 12, true, 'PARCIAL',
                    TIMESTAMPTZ '2026-03-15 12:05:00-05')""", sesion, celular);
        jdbc.update("""
                INSERT INTO anotacion_rechazada (anotacion_uuid, huella, sesion_id, consecutivo, motivo, contenido,
                    recibido_en)
                VALUES (gen_random_uuid(), 'h', ?, 1, 'corrupta', '{}', TIMESTAMPTZ '2026-03-15 12:01:00-05')""", sesion);

        String filas = web.perform(get("/web/tablero/filas").param("jornada", jornada.toString()).with(GERENTE))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(filas).doesNotContain("<html").contains("1 de 4", "25 %", celular.toString(), "T1-S1", "T1-S2");
        assertThat(soloNumeros(filaDe(filas, celular.toString()))).containsExactly(4, 1, 3, 25, 1, 1, 1);
        assertThat(soloNumeros(filaDe(filas, ">T1-S1<"))).containsExactly(2, 1, 1, 50, 0);
        assertThat(soloNumeros(filaDe(filas, ">T1-S2<"))).containsExactly(2, 0, 2, 0, 1);

        // Reasignar reemplaza: con una sola sección quedan 2 celdas.
        web.perform(post("/web/asignacion").with(ADMINISTRADOR).with(csrf()).param("jornada", jornada.toString())
                        .param("dispositivo", celular.toString()).param("secciones", "T1-S1").param("campos", "tallos botones"))
                .andExpect(status().isFound());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM asignacion WHERE dispositivo_id = ? AND jornada = ?",
                Integer.class, celular, jornada)).isEqualTo(2);
    }

    @Test
    void el_gerente_resuelve_un_trabajo_una_sola_vez() throws Exception {
        UUID anotacion = UUID.fromString("0192f0a0-0000-7000-8000-0000000000f6");
        jdbc.update("""
                INSERT INTO trabajo_gerente (anotacion_uuid, dispositivo_id, seccion_id, jornada, codigos, abierto_en)
                VALUES (?, gen_random_uuid(), 'B9', DATE '2026-03-16', ARRAY['divergencia'], now())""", anotacion);

        web.perform(get("/web/trabajos").with(GERENTE)).andExpect(status().isOk())
                .andExpect(content().string(Matchers.allOf(Matchers.containsString(anotacion.toString()),
                        Matchers.containsString("divergencia"), Matchers.not(Matchers.containsString("Resuelto.")))));
        web.perform(post("/web/trabajos/" + anotacion + "/resolver").with(GERENTE).with(csrf()).header("HX-Request", "true"))
                .andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("Resuelto.")));
        web.perform(post("/web/trabajos/" + anotacion + "/resolver").with(como("otro-gerente", "gerente")).with(csrf())
                        .header("HX-Request", "true"))
                .andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("otro gerente")));

        assertThat(jdbc.queryForObject("SELECT resuelto_por FROM trabajo_gerente WHERE anotacion_uuid = ?", String.class,
                anotacion)).isEqualTo("gerente-web");
        assertThat(jdbc.queryForList("SELECT actor FROM bitacora WHERE evento = 'trabajo_resuelto' AND objeto = ?",
                String.class, anotacion.toString())).containsExactly("gerente-web");
        web.perform(get("/web/trabajos").with(GERENTE))
                .andExpect(content().string(Matchers.not(Matchers.containsString(anotacion.toString()))));
    }

    private EstadoDeDispositivo estado(UUID celular) {
        return dispositivos.todos().stream().filter(d -> d.dispositivo().equals(celular)).findFirst().orElseThrow().estado();
    }

    /** La fila {@code <tr>} de la tabla que contiene {@code marca}. */
    private static String filaDe(String html, String marca) {
        int i = html.indexOf(marca);
        assertThat(i).as("la fila de " + marca).isNotNegative();
        return html.substring(html.lastIndexOf("<tr", i), html.indexOf("</tr>", i));
    }

    /** Los números de las celdas de una fila, en orden, sin el id del celular ni de la sección. */
    private static List<Integer> soloNumeros(String fila) {
        return Pattern.compile("<td[^>]*>\\s*(?:<progress[^>]*></progress>\\s*<span>)?(\\d+)(?: %)?")
                .matcher(fila).results().map(m -> Integer.parseInt(m.group(1))).toList();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootTest (sin puerto): arranca el contexto completo con un entorno web simulado; las peticiones van por
 *   MockMvc, sin red.
 * - @Testcontainers(disabledWithoutDocker = true), @Container, @ServiceConnection: PostgreSQL 16 en Docker con la
 *   conexión puesta sola; sin Docker la clase se salta y nada de esto queda probado.
 * - @Autowired WebApplicationContext / JdbcTemplate / RegistroDeDispositivos / Bitacora: Spring inyecta el
 *   contexto, el acceso SQL directo y las API públicas de los módulos dispositivos y bitacora.
 * - @BeforeEach: corre antes de cada prueba; arma un MockMvc nuevo.
 * - MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()): MockMvc sobre la aplicación real, con
 *   los filtros de Spring Security puestos (sin apply, la seguridad no actuaría).
 * - oidcLogin().idToken(…).authorities(…) (spring-security-test): simula una sesión ya iniciada con Keycloak, con
 *   ese sub, ese preferred_username y ese rol, sin pasar por el inicio de sesión.
 * - .with(csrf()): agrega a la petición un token CSRF válido; sin él, un POST de la web sale 403.
 * - web.perform(get(…) / post(…)).andExpect(…): hace la petición simulada y comprueba la respuesta.
 * - status().isFound(): 302 (redirección); isForbidden(): 403; isUnprocessableContent(): 422 (los datos no sirven).
 * - redirectedUrl(…) / getRedirectedUrl(): a dónde manda la redirección; se revisa que lleve client_id, PKCE
 *   (code_challenge_method=S256), scope y redirect_uri.
 * - header().string("Content-Security-Policy", …) y "X-Frame-Options", "DENY": que salgan las cabeceras de seguridad.
 * - content().string(Matchers.containsString(…)) (Hamcrest): el cuerpo HTML contiene ese texto; Matchers.allOf
 *   exige varias condiciones y Matchers.not niega una (por ejemplo, que un fragmento htmx no traiga «<html»).
 * - .header("HX-Request", "true"): la cabecera que manda htmx; con ella el controlador responde solo el fragmento,
 *   y sin ella redirige como un formulario normal.
 * - .param(…): campos de formulario (jornada, dispositivo, secciones, campos).
 * - jdbc.update(""" INSERT … """, …): bloques de texto de Java con SQL que siembran hechos, trabajos, sesiones y
 *   rechazos directo en la base; gen_random_uuid() y now() son funciones de PostgreSQL, ARRAY['…'] un arreglo de
 *   texto y TIMESTAMPTZ '…' una fecha con zona.
 * - jdbc.queryForList(sql, String.class, …) / queryForObject: leen la bitácora o cuentan filas para comprobar.
 * - getResponse().getContentAsString(): el HTML devuelto como texto, para buscar en él.
 * - Pattern.compile(regex).matcher(fila).results(): todas las coincidencias de la expresión regular en la fila
 *   (Java 9+); cada m.group(1) es el número de una celda td, con o sin barra progress y signo de porcentaje.
 * - indexOf / lastIndexOf("<tr", i) / substring: recorta del HTML la fila de la tabla que contiene la marca.
 */
