package co.florlogic.finca.infraestructura;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * La web del gerente y del administrador (F4, CT-2, CT-13): inicia sesión con el cliente {@code web} de Keycloak
 * (código de autorización con PKCE; Keycloak pide contraseña y TOTP) y guarda la sesión en una cookie, con CSRF. Vive
 * en {@code /web/**}; el resto del nodo sigue siendo el servidor de recursos de {@link SeguridadDelNodo}.
 *
 * <p>Los roles salen del token de acceso que Keycloak entrega junto al de identidad: se valida con el mismo
 * {@link JwtDecoder} del servidor de recursos (firma, emisor, audiencia {@code backend-finca}) y se leen sus
 * {@code realm_access.roles}. Así la web y la API juzgan el rol con una sola regla.
 *
 * <p>El registro del cliente se arma aquí y no con {@code issuer-uri}: con él, Spring pediría el discovery a Keycloak
 * al arrancar, y el backend arranca sin Keycloak (como con las llaves). El navegador va a Keycloak por el gateway (la
 * URL del emisor); el backend canjea el código por la red interna.
 */
@Configuration
public class WebDelNodo {

    static final String REGISTRO = "keycloak";
    static final String INICIO_DE_SESION = "/web/oauth2/authorization/" + REGISTRO;

    @Bean
    @Order(1)
    SecurityFilterChain cadenaDeLaWeb(HttpSecurity http, ClientRegistrationRepository registros,
            OAuth2UserService<OidcUserRequest, OidcUser> personas) throws Exception {
        OidcClientInitiatedLogoutSuccessHandler alSalir = new OidcClientInitiatedLogoutSuccessHandler(registros);
        alSalir.setPostLogoutRedirectUri("{baseUrl}/web/");
        return http
                .securityMatcher("/web/**")
                .authorizeHttpRequests(rutas -> rutas
                        .requestMatchers("/web/estatico/**").permitAll()
                        .requestMatchers("/web/dispositivos/**", "/web/asignacion/**").hasRole("administrador")
                        .requestMatchers("/web/trabajos/**").hasRole("gerente")
                        .requestMatchers("/web", "/web/", "/web/tablero/**").hasAnyRole("gerente", "administrador")
                        // Igual que en la API: lo que no tiene regla no existe para nadie.
                        .anyRequest().denyAll())
                .oauth2Login(entrar -> entrar
                        .loginPage(INICIO_DE_SESION)   // sin pantalla propia: directo a Keycloak
                        .authorizationEndpoint(a -> a.baseUri("/web/oauth2/authorization"))
                        .redirectionEndpoint(r -> r.baseUri("/web/login/oauth2/code/*"))
                        .userInfoEndpoint(u -> u.oidcUserService(personas))
                        .defaultSuccessUrl("/web/", false))
                .logout(salir -> salir.logoutUrl("/web/salir").logoutSuccessHandler(alSalir))
                // CSRF por la sesión (el de Spring): los formularios lo llevan oculto y htmx en la cabecera X-CSRF-TOKEN.
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(h -> h
                        // Solo scripts y estilos propios: htmx va servido desde aquí y sin estilos en línea.
                        .contentSecurityPolicy(c -> c.policyDirectives(
                                "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; "
                                        + "form-action 'self'; frame-ancestors 'none'; base-uri 'none'; object-src 'none'"))
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
                .build();
    }

    @Bean
    ClientRegistrationRepository registrosDeLaWeb(
            @Value("${florlogic.web.cliente:web}") String cliente,
            @Value("${florlogic.web.secreto:}") String secreto,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String emisor,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String llaves,
            @Value("${florlogic.identidad.interna}") String interna) {
        return new InMemoryClientRegistrationRepository(ClientRegistration.withRegistrationId(REGISTRO)
                .clientId(cliente)
                .clientSecret(secreto)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/web/login/oauth2/code/{registrationId}")
                .scope("openid", "profile")
                .authorizationUri(emisor + "/protocol/openid-connect/auth")
                .tokenUri(interna + "/protocol/openid-connect/token")
                .jwkSetUri(llaves)
                .issuerUri(emisor)   // el iss del token de identidad se compara con este
                .userNameAttributeName("sub")
                .providerConfigurationMetadata(Map.of("end_session_endpoint", emisor + "/protocol/openid-connect/logout"))
                .clientName("FlorLogic")
                // El cliente web de Keycloak exige PKCE S256 (realm-florlogic.json), también siendo confidencial.
                .clientSettings(ClientRegistration.ClientSettings.builder().requireProofKey(true).build())
                .build());
    }

    /** La persona de la web: la del token de identidad, con los roles del realm que trae el token de acceso. */
    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> personasDeLaWeb(JwtDecoder tokens) {
        OidcUserService delegado = new OidcUserService();
        return pedido -> {
            OidcUser persona = delegado.loadUser(pedido);
            Set<GrantedAuthority> permisos = new HashSet<>(persona.getAuthorities());
            permisos.addAll(rolesDelAcceso(tokens, pedido.getAccessToken().getTokenValue()));
            return new DefaultOidcUser(permisos, persona.getIdToken(), persona.getUserInfo(), "sub");
        };
    }

    static Collection<GrantedAuthority> rolesDelAcceso(JwtDecoder tokens, String acceso) {
        return SeguridadDelNodo.roles(tokens.decode(acceso));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Configuration / @Bean: la clase fabrica para Spring tres piezas: la cadena de la web, el registro del cliente
 *   de Keycloak y el servicio que arma la persona que inició sesión.
 * - @Order(1): esta cadena se prueba antes que la de SeguridadDelNodo (@Order(2)).
 * - securityMatcher (todas las rutas bajo /web): esta cadena solo atiende esas rutas; lo demás sigue a la otra.
 * - OidcClientInitiatedLogoutSuccessHandler + setPostLogoutRedirectUri("{baseUrl}/web/"): al salir, además de
 *   cerrar la sesión local, manda el navegador a Keycloak a cerrar la suya (end_session_endpoint) y luego vuelve a
 *   /web/. {baseUrl} lo reemplaza Spring por el esquema, host y puerto que vio el navegador.
 * - requestMatchers(…).permitAll() / hasRole / hasAnyRole: los estáticos (htmx, CSS) bajo /web/estatico no piden
 *   sesión; dispositivos y asignación solo para administrador; trabajos solo para gerente; el inicio y el tablero
 *   para ambos; anyRequest().denyAll() niega el resto.
 * - oauth2Login(…): inicio de sesión con OpenID Connect. loginPage manda directo a Keycloak (sin pantalla propia);
 *   authorizationEndpoint y redirectionEndpoint mueven bajo /web las rutas que Spring usa para iniciar el viaje a
 *   Keycloak y para recibir el código de vuelta (el asterisco final es el id del registro, keycloak);
 *   userInfoEndpoint(oidcUserService(personas)) usa personasDeLaWeb para armar la persona;
 *   defaultSuccessUrl("/web/", false): tras entrar va a la página que pidió, o a /web/ si no pidió ninguna.
 * - logout(logoutUrl("/web/salir")): la ruta de salida (un POST con CSRF).
 * - CSRF: aquí no se apaga (a diferencia de la API) porque la web usa cookie de sesión.
 * - contentSecurityPolicy(…): cabecera Content-Security-Policy; el navegador solo ejecuta scripts, estilos,
 *   imágenes y formularios del mismo origen ('self'), no deja meter la página en un marco (frame-ancestors 'none'),
 *   ni cambiar la base de las URL (base-uri 'none'), ni cargar plugins (object-src 'none').
 * - ReferrerPolicy.SAME_ORIGIN: la cabecera Referer solo se manda a este mismo sitio, nunca a uno externo.
 * - ClientRegistration.withRegistrationId("keycloak"): describe el cliente OAuth2 a mano:
 *   clientId y clientSecret (el secreto llega por variable de entorno, sin valor en el repo);
 *   CLIENT_SECRET_BASIC: el backend se identifica ante Keycloak con usuario y clave en la cabecera Authorization;
 *   AUTHORIZATION_CODE: el flujo «código de autorización» (el navegador trae un código y el backend lo canjea);
 *   redirectUri con {baseUrl} y {registrationId}: plantillas que Spring llena al vuelo;
 *   scope("openid", "profile"): pide el token de identidad y los datos básicos (nombre de usuario);
 *   authorizationUri usa el emisor público (el navegador va por el gateway) y tokenUri la URL interna (el canje
 *   es de servidor a servidor por la red de Docker); jwkSetUri, de dónde bajar las llaves públicas;
 *   issuerUri: el iss que debe traer el token de identidad; userNameAttributeName("sub"): el nombre de la persona
 *   es su sub; providerConfigurationMetadata(end_session_endpoint): la URL de cierre de sesión que el manejador de
 *   salida necesita y que el discovery habría dado; requireProofKey(true): PKCE, el navegador manda un reto
 *   (code_challenge S256) que solo este backend puede completar al canjear el código.
 * - InMemoryClientRegistrationRepository: guarda ese único registro en memoria.
 * - @Value("${…:web}") y @Value("${florlogic.web.secreto:}"): lee claves de application.yaml; lo que va tras los dos
 *   puntos es el valor por defecto (vacío para el secreto). Las claves sin dos puntos son obligatorias.
 * - OAuth2UserService<OidcUserRequest, OidcUser>: la función que, tras el canje, devuelve la persona. Aquí delega en
 *   OidcUserService (el de Spring, que valida el token de identidad y pide userinfo), le suma los roles leídos del
 *   token de acceso validado con el mismo JwtDecoder de la API, y arma un DefaultOidcUser con «sub» como nombre.
 * - JwtDecoder.decode: valida firma, vigencia, emisor y audiencia del token; si falla, lanza JwtException y la
 *   persona no entra.
 */
