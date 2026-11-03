package co.florlogic.finca.reglas;

import java.util.List;
import java.util.Optional;

/**
 * El juicio de las reglas (A-99): los códigos de marca, en minúscula como en el contrato (vacío si no hay nada que
 * marcar), o el motivo del rechazo. Un rechazo no lleva marcas.
 */
public record ResultadoDeEvaluacion(List<String> marcas, Optional<String> motivoRechazo) {

    public ResultadoDeEvaluacion {
        marcas = List.copyOf(marcas);
    }

    public static ResultadoDeEvaluacion rechazo(String motivo) {
        return new ResultadoDeEvaluacion(List.of(), Optional.of(motivo));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera constructor, marcas(), motivoRechazo(), equals, hashCode y
 *   toString. El equals por valor es lo que deja a la prueba comparar con isEqualTo(ResultadoDeEvaluacion.rechazo(…)).
 * - Optional<String> motivoRechazo: «puede que haya, puede que no»; Optional.of(motivo) en un rechazo y
 *   Optional.empty() cuando la anotación se acepta (con o sin marcas).
 * - public ResultadoDeEvaluacion { marcas = List.copyOf(marcas); }: constructor compacto; guarda una copia inmutable.
 */
