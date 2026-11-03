package co.florlogic.finca.infraestructura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * La aplicación de captura del celular (F8, CT-4): la PWA de {@code app-captura} servida por el nodo en {@code /app/},
 * desde {@code static/app}. Comparte origen con {@code /auth} e {@code /ingesta}, así que el navegador no pide CORS
 * (OBS-1-1 del gateway).
 *
 * <p>Aquí solo hay archivos: nadie necesita sesión para bajarlos, porque los datos no viajan por esta ruta. El celular
 * entra con el cliente {@code celular} de Keycloak (PKCE, en el navegador) y entrega con su token por {@code /ingesta},
 * donde {@link SeguridadDelNodo} exige el rol {@code operador}.
 */
@Configuration
public class AppDelNodo implements WebMvcConfigurer {

    /** Antes que la de {@link WebDelNodo} y la de {@link SeguridadDelNodo}: solo atiende {@code /app}. */
    @Bean
    @Order(0)
    SecurityFilterChain cadenaDeLaApp(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/app", "/app/**")
                .authorizeHttpRequests(rutas -> rutas
                        .requestMatchers(HttpMethod.GET, "/app", "/app/**").permitAll()
                        // Solo se leen archivos: cualquier otro método no existe para nadie.
                        .anyRequest().denyAll())
                // Sin cookies ni formularios: no hay sesión que falsificar.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(h -> h
                        // Como la web: solo lo propio. connect-src 'self' alcanza para Keycloak y la ingesta, que
                        // pasan por el mismo gateway; worker-src y manifest-src, para el service worker y el manifiesto.
                        .contentSecurityPolicy(c -> c.policyDirectives(
                                "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; "
                                        + "connect-src 'self'; worker-src 'self'; manifest-src 'self'; "
                                        + "form-action 'self'; frame-ancestors 'none'; base-uri 'none'; object-src 'none'"))
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
                .build();
    }

    /** {@code /app} lleva a {@code /app/}: las rutas de la PWA son relativas y sin la barra apuntarían a la raíz. */
    @Override
    public void addViewControllers(ViewControllerRegistry rutas) {
        rutas.addRedirectViewController("/app", "/app/");
        rutas.addViewController("/app/").setViewName("forward:/app/index.html");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Configuration / @Bean: la clase fabrica para Spring la cadena de seguridad de /app y, como WebMvcConfigurer,
 *   agrega dos rutas a Spring MVC.
 * - @Order(0): Spring Security prueba primero la cadena de menor número; esta va antes que la web (@Order(1)) y que
 *   la API (@Order(2)), y como tiene securityMatcher solo se queda con /app y lo que cuelga de ella.
 * - requestMatchers(HttpMethod.GET, …).permitAll(): leer los archivos no pide sesión ni token; anyRequest().denyAll()
 *   niega POST, PUT y el resto (403). El gateway, además, responde 405 a todo lo que no sea GET.
 * - csrf(disable), sessionCreationPolicy(STATELESS), requestCache(disable): sin sesión ni cookie; nada que proteger
 *   con CSRF y nada que recordar entre peticiones.
 * - httpBasic / formLogin (disable): sin pantalla de inicio de sesión propia ni usuario y clave en la cabecera.
 * - contentSecurityPolicy(…): cabecera Content-Security-Policy. El navegador solo ejecuta scripts, estilos, imágenes,
 *   service worker (worker-src) y manifiesto (manifest-src) del mismo origen; connect-src limita fetch al mismo
 *   origen; frame-ancestors 'none' impide meter la app en un marco; base-uri 'none' y object-src 'none' cierran la
 *   etiqueta base y los plugins. Ningún script en línea pasa: por eso limpiar.html carga js/limpiar.js.
 * - ReferrerPolicy.SAME_ORIGIN: la cabecera Referer solo se manda a este mismo sitio.
 * - addRedirectViewController("/app", "/app/"): responde 302 hacia /app/.
 * - addViewController("/app/").setViewName("forward:/app/index.html"): /app/ entrega index.html por dentro, sin que
 *   el navegador vea otra URL (la de la PWA instalada y la de regreso de Keycloak siguen siendo /app/).
 * - El manifiesto de la PWA se llama manifest.json (en la demo, manifest.webmanifest): ni Tomcat ni Spring conocen la
 *   extensión .webmanifest y lo entregarían como application/octet-stream; .json sale como application/json, que los
 *   navegadores aceptan para el manifiesto.
 */
