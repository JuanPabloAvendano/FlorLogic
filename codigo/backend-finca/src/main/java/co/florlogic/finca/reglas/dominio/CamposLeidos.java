package co.florlogic.finca.reglas.dominio;

import java.util.Map;

/**
 * Lo que el lector sacó del contenido de una anotación, ya sin JSON: cómo viene cada campo esperado y si trae motivo
 * de ausencia.
 *
 * @param datosValidos {@code false} si no hay un objeto {@code datos} que leer: es un rechazo, no una marca (A-101)
 */
public record CamposLeidos(boolean datosValidos, Map<String, Presencia> campos, boolean hayMotivo) {

    public CamposLeidos {
        campos = Map.copyOf(campos);
    }

    public static CamposLeidos invalidos() {
        return new CamposLeidos(false, Map.of(), false);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera constructor, datosValidos(), campos(), hayMotivo(), equals, hashCode
 *   y toString.
 * - public CamposLeidos { campos = Map.copyOf(campos); }: constructor compacto; guarda una copia inmutable del mapa
 *   campo → Presencia (Map.copyOf no admite llaves ni valores null).
 * - static invalidos(): fábrica con nombre para el caso «no hubo datos que leer», en vez de un constructor con tres
 *   argumentos sueltos.
 */
