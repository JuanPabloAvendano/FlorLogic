package co.florlogic.finca.infraestructura;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

/**
 * El nodo como servidor de recursos (CT-2, TEC-14): toda petición trae un JWT de Keycloak (TEC-32), salvo la salud y
 * las métricas, que solo se ven por la red interna (el gateway devuelve 404 a {@code /actuator}). Firma, vigencia,
 * emisor y audiencia los valida Spring con lo que dice {@code application.yaml}; aquí se decide qué rol pide cada ruta.
 *
 * <p>Los roles son los del realm ({@code realm_access.roles}): {@code operador}, {@code gerente},
 * {@code administrador}. La persona es el {@code sub} del token, que Spring pone como nombre del {@code Principal}
 * (contrato A-51). Los errores salen como el contrato los pide (A-22): {@code 401 credencial-invalida} antes que
 * {@code 403 sin-permiso} {@code ingesta_entregar}.
 */
@Configuration
public class SeguridadDelNodo {

    static final String CREDENCIAL_INVALIDA = "https://florlogic.co/problemas/ingesta/credencial-invalida";
    static final String SIN_PERMISO = "https://florlogic.co/problemas/ingesta/sin-permiso";

    /**
     * Va después de la de {@link AppDelNodo} y la de {@link WebDelNodo}: esta no tiene {@code securityMatcher} y atrapa
     * todo lo demás.
     */
    @Bean
    @Order(2)
    SecurityFilterChain cadenaDeSeguridad(HttpSecurity http) throws Exception {
        return http
                // Sin cookies ni sesión: cada petición trae su token, así que CSRF no aplica (la web de F4 tiene la suya).
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas
                        // El reenvío interno a /error ya pasó por estas reglas: sin esto, un 405 o un 500 saldría 403.
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info",
                                "/actuator/prometheus").permitAll()
                        .requestMatchers("/ingesta/**").hasRole("operador")
                        // Lo que no tiene regla no existe para nadie: una ruta nueva se abre aquí, a propósito.
                        .anyRequest().denyAll())
                .oauth2ResourceServer(recursos -> recursos
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(rolesDelRealm()))
                        .authenticationEntryPoint(credencialInvalida())
                        .accessDeniedHandler(sinPermiso()))
                .exceptionHandling(e -> e.authenticationEntryPoint(credencialInvalida()).accessDeniedHandler(sinPermiso()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .headers(Customizer.withDefaults())
                .build();
    }

    /** {@code realm_access.roles} de Keycloak → {@code ROLE_<rol>}; el nombre del {@code Principal} sigue siendo el sub. */
    static JwtAuthenticationConverter rolesDelRealm() {
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(SeguridadDelNodo::roles);
        return conversor;
    }

    static Collection<GrantedAuthority> roles(Jwt jwt) {
        if (!(jwt.getClaims().get("realm_access") instanceof Map<?, ?> realm)
                || !(realm.get("roles") instanceof List<?> roles)) {
            return List.of();
        }
        return roles.stream()
                .filter(String.class::isInstance)
                .map(rol -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + rol))
                .toList();
    }

    /** Sin token, con uno vencido, mal firmado, de otro emisor o para otra audiencia: 401, y el celular renueva (A-41). */
    private static AuthenticationEntryPoint credencialInvalida() {
        return (peticion, respuesta, causa) -> {
            respuesta.setHeader("WWW-Authenticate", "Bearer");
            problema(respuesta, HttpStatus.UNAUTHORIZED, CREDENCIAL_INVALIDA, "Credencial inválida o vencida", null);
        };
    }

    /**
     * Token válido sin el rol de la ruta: 403. En {@code /ingesta/} lleva la regla {@code ingesta_entregar} (A-22); en
     * una ruta sin regla no se nombra ninguna, porque no hay permiso que pedir.
     */
    private static AccessDeniedHandler sinPermiso() {
        return (peticion, respuesta, causa) -> problema(respuesta, HttpStatus.FORBIDDEN, SIN_PERMISO, "Sin permiso",
                peticion.getRequestURI().startsWith("/ingesta/") ? "ingesta_entregar" : null);
    }

    // Texto fijo, sin nada de la petición ni del token: no hay qué escapar ni qué filtrar a la respuesta.
    private static void problema(HttpServletResponse respuesta, HttpStatus estado, String tipo, String titulo,
            String regla) throws IOException {
        respuesta.setStatus(estado.value());
        respuesta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.setHeader("Cache-Control", "no-store");
        String extra = regla == null ? "" : ",\"regla\":\"" + regla + "\"";
        respuesta.getWriter().write("{\"type\":\"" + tipo + "\",\"title\":\"" + titulo + "\",\"status\":" + estado.value()
                + extra + "}");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Configuration / @Bean: la clase fabrica para Spring la cadena de seguridad que devuelve cadenaDeSeguridad.
 * - @Order(2): orden entre cadenas; Spring Security prueba primero la de menor número (la web, @Order(1)) y esta
 *   recibe todo lo que la web no reclamó.
 * - SecurityFilterChain / HttpSecurity: la lista de filtros que revisa cada petición antes de llegar al
 *   controlador; HttpSecurity es el constructor con el que se arma, encadenando métodos y cerrando con build().
 * - .csrf(AbstractHttpConfigurer::disable): apaga la protección CSRF (falsificación de peticiones desde otra
 *   página), que solo tiene sentido con cookies; aquí cada petición trae su token.
 * - SessionCreationPolicy.STATELESS: Spring no crea sesión HTTP ni cookie; cada petición se juzga sola.
 * - authorizeHttpRequests(…): las reglas de quién entra a qué, en orden; gana la primera que coincide.
 * - dispatcherTypeMatchers(DispatcherType.ERROR).permitAll(): cuando algo falla, el servidor reenvía por dentro a
 *   /error; ese reenvío (tipo ERROR) ya fue juzgado, así que se deja pasar para que un 405 o un 500 salgan como tales.
 * - requestMatchers(…).permitAll(): la salud (/actuator/health y todo lo que cuelga de ella), /actuator/info y
 *   /actuator/prometheus (las métricas que raspa Prometheus) no piden token.
 * - requestMatchers("/ingesta" y todo lo que cuelga) .hasRole("operador"): exige la autoridad ROLE_operador.
 * - anyRequest().denyAll(): todo lo demás se niega aunque traiga token válido.
 * - oauth2ResourceServer(…jwt(…)): el nodo acepta la cabecera Authorization: Bearer con un JWT (token firmado);
 *   jwtAuthenticationConverter cambia cómo se sacan los permisos del token (rolesDelRealm, abajo).
 * - authenticationEntryPoint / accessDeniedHandler: qué se responde cuando no hay credencial válida (401) y
 *   cuando la hay pero falta el rol (403). Se ponen dos veces: dentro de oauth2ResourceServer cubren los fallos
 *   del token, y en exceptionHandling los de las reglas de ruta (por ejemplo, petición sin token a una ruta protegida).
 * - httpBasic / formLogin / logout disable: sin usuario y clave por cabecera, sin formulario de inicio y sin
 *   ruta de salida en la API.
 * - headers(Customizer.withDefaults()): deja las cabeceras de seguridad por defecto de Spring (X-Frame-Options,
 *   X-Content-Type-Options, Cache-Control en respuestas protegidas…).
 * - JwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(SeguridadDelNodo::roles): por defecto Spring lee
 *   los permisos de la reclamación scope; aquí se le da la función roles para que los lea de realm_access.roles.
 * - instanceof Map<?, ?> realm / instanceof List<?> roles: patrón de Java 16+; comprueba el tipo y nombra la
 *   variable a la vez. Los <?> dicen «de cualquier tipo de elemento», porque el JSON del token no lo garantiza.
 * - String.class::isInstance: descarta los elementos de la lista que no sean texto antes de crear permisos.
 * - SimpleGrantedAuthority("ROLE_" + rol): hasRole("operador") busca exactamente «ROLE_operador»; por eso el prefijo.
 * - .toList(): Stream.toList() de Java 16, devuelve una lista que no se puede modificar.
 * - (peticion, respuesta, causa) -> {…}: lambda que implementa AuthenticationEntryPoint o AccessDeniedHandler;
 *   causa (la excepción) se ignora a propósito para no filtrar el detalle del fallo al cliente.
 * - "WWW-Authenticate: Bearer": cabecera que el estándar HTTP pide en un 401; le dice al celular que use un token
 *   Bearer (y que lo renueve).
 * - MediaType.APPLICATION_PROBLEM_JSON_VALUE: «application/problem+json», el formato estándar de errores HTTP
 *   (RFC 9457, Problem Details) con los campos type, title y status; aquí se agrega regla.
 * - "Cache-Control: no-store": que ni el navegador ni ningún intermediario guarden la respuesta de error.
 * - getWriter().write(…): escribe el JSON a mano, sin Jackson, porque todo el texto es fijo.
 */
