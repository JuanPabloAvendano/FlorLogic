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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @ControllerAdvice (Spring Web): esta clase no atiende rutas; sus métodos se aplican a todos los controladores.
 *   Aquí sirve para poner «persona» y «roles» en el modelo de cada vista Thymeleaf, una sola vez.
 * - @ModelAttribute("persona") / @ModelAttribute("roles"): antes de cada vista, Spring llama el método y guarda lo
 *   que devuelve en el modelo con ese nombre; la plantilla lo lee como persona y roles.
 * - Authentication: lo que Spring Security sabe de quien hizo la petición; llega null si nadie inició sesión.
 * - instanceof OidcUser oidc: patrón de Java 16+; si el principal es un usuario OpenID Connect (el que deja el
 *   inicio de sesión con Keycloak en la web), lo nombra oidc sin hacer el cast a mano.
 * - getPreferredUsername() / getSubject(): el nombre de usuario de Keycloak (preferred_username) y, si no viene,
 *   el sub (identificador estable de la persona).
 * - getAuthorities().stream()…filter(startsWith("ROLE_"))…substring(5): de los permisos de Spring se quedan solo
 *   los roles (los que WebDelNodo puso con el prefijo ROLE_) y se les quita ese prefijo de 5 letras, para que la
 *   plantilla pregunte por «gerente» o «administrador». Collectors.toSet() los junta en un Set sin repetidos.
 */
