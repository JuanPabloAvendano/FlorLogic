package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import java.security.Principal;

/**
 * Quién entrega, sacado de la credencial (contrato A-51). Con Keycloak (P3) será el {@code sub} del JWT, que Spring
 * Security pone como nombre del {@link Principal} (CT-2).
 */
final class Personas {

    /** Sin credencial no hay persona. Hasta P3 es la de todo el nodo; desde P3, el filtro devuelve 401 antes. */
    static final String SIN_CREDENCIAL = "sin-credencial";

    private Personas() {
    }

    static String de(Principal quien) {
        return quien == null ? SIN_CREDENCIAL : quien.getName();
    }
}
