package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import java.security.Principal;

/**
 * Quién entrega, sacado de la credencial (contrato A-51). Con Keycloak es el {@code sub} del JWT, que Spring Security
 * pone como nombre del {@link Principal} (CT-2).
 */
final class Personas {

    /** Sin credencial no hay persona: solo pasa en las pruebas sin seguridad; en el nodo, el filtro devuelve 401 antes. */
    static final String SIN_CREDENCIAL = "sin-credencial";

    private Personas() {
    }

    static String de(Principal quien) {
        return quien == null ? SIN_CREDENCIAL : quien.getName();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - java.security.Principal: la identidad autenticada que entrega Spring Security; getName() es su nombre (el sub
 *   del token).
 * - final class + private Personas(): clase de utilidades que no se hereda ni se instancia.
 */
