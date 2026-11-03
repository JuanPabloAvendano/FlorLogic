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
