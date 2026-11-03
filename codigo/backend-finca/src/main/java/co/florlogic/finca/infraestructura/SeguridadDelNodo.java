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

    /** Va después de la de {@link WebDelNodo}: esta no tiene {@code securityMatcher} y atrapa todo lo demás. */
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

    /** Token válido sin el rol de la ruta: 403 {@code ingesta_entregar} (A-22); la persona sigue, otra credencial sí. */
    private static AccessDeniedHandler sinPermiso() {
        return (peticion, respuesta, causa) ->
                problema(respuesta, HttpStatus.FORBIDDEN, SIN_PERMISO, "Sin permiso", "ingesta_entregar");
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
