package co.florlogic.finca.infraestructura;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Quién está en la web y con qué roles, para la plantilla (el menú muestra solo lo que su rol abre). Thymeleaf 3.1 ya no
 * deja leer la petición desde la plantilla, y los controladores no ven Spring Security ({@code CapasTest}): lo pone
 * aquí, una vez, en el modelo de toda vista. En la API no hay vista y nadie lo lee.
 */
@ControllerAdvice
class PersonaDeLaWeb {

    @ModelAttribute("persona")
    String persona(Authentication autenticacion) {
        if (autenticacion != null && autenticacion.getPrincipal() instanceof OidcUser oidc) {
            return oidc.getPreferredUsername() != null ? oidc.getPreferredUsername() : oidc.getSubject();
        }
        return null;
    }

    @ModelAttribute("roles")
    Set<String> roles(Authentication autenticacion) {
        if (autenticacion == null) {
            return Set.of();
        }
        return autenticacion.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_")).map(a -> a.substring(5)).collect(Collectors.toSet());
    }
}
