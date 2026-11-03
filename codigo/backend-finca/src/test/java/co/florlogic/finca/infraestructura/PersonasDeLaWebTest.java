package co.florlogic.finca.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

/**
 * Los roles de la web salen del token de acceso de Keycloak, validado como en la API (F4, CT-2): un token de acceso que
 * no pasa la validación no deja entrar, aunque el de identidad sea bueno. Sin Docker ni Keycloak.
 */
class PersonasDeLaWebTest {

    private final ClientRegistration registro = new WebDelNodo().registrosDeLaWeb("web", "s",
            "http://localhost/auth/realms/florlogic", "http://keycloak:8080/certs", "http://keycloak:8080/auth/realms/florlogic")
            .findByRegistrationId(WebDelNodo.REGISTRO);

    private OidcUserRequest pedido(String acceso) {
        Instant ahora = Instant.now();
        OidcIdToken identidad = new OidcIdToken("id", ahora, ahora.plusSeconds(60),
                Map.of("sub", "ger-1", "preferred_username", "gerente1", "iss", "http://localhost/auth/realms/florlogic"));
        return new OidcUserRequest(registro,
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, acceso, ahora, ahora.plusSeconds(60)), identidad);
    }

    private static Jwt acceso(Object roles) {
        return Jwt.withTokenValue("acceso").header("alg", "RS256").subject("ger-1")
                .claim("realm_access", Map.of("roles", roles)).build();
    }

    @Test
    void los_roles_del_realm_vienen_del_token_de_acceso_y_la_persona_es_el_sub() {
        JwtDecoder decodificador = valor -> acceso(List.of("gerente", "offline_access"));

        OidcUser persona = new WebDelNodo().personasDeLaWeb(decodificador).loadUser(pedido("acceso"));

        assertThat(persona.getName()).isEqualTo("ger-1");
        assertThat(persona.getPreferredUsername()).isEqualTo("gerente1");
        assertThat(persona.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_gerente", "ROLE_offline_access").doesNotContain("ROLE_administrador");
    }

    @Test
    void un_token_de_acceso_invalido_no_deja_entrar() {
        JwtDecoder decodificador = valor -> {
            throw new JwtException("otra audiencia");
        };

        assertThatThrownBy(() -> new WebDelNodo().personasDeLaWeb(decodificador).loadUser(pedido("malo")))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void el_registro_pide_pkce_y_canjea_por_la_red_interna() {
        assertThat(registro.getClientSettings().isRequireProofKey()).isTrue();
        assertThat(registro.getProviderDetails().getAuthorizationUri())
                .isEqualTo("http://localhost/auth/realms/florlogic/protocol/openid-connect/auth");
        assertThat(registro.getProviderDetails().getTokenUri())
                .isEqualTo("http://keycloak:8080/auth/realms/florlogic/protocol/openid-connect/token");
        assertThat(registro.getProviderDetails().getIssuerUri()).isEqualTo("http://localhost/auth/realms/florlogic");
    }
}
