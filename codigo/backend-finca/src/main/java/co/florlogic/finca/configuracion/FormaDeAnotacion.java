package co.florlogic.finca.configuracion;

import java.util.List;
import java.util.Objects;

/**
 * La forma de un tipo de anotación (A-88; forma {@code captura.campo} v1, COH-164): qué campos de {@code datos} se
 * esperan y cuál trae el motivo cuando falta un valor.
 */
public record FormaDeAnotacion(String tipo, int version, List<String> campos, String campoMotivoAusencia) {

    public FormaDeAnotacion {
        Objects.requireNonNull(tipo, "tipo");
        campos = List.copyOf(campos);
        Objects.requireNonNull(campoMotivoAusencia, "campoMotivoAusencia");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: tipo(), campos()…), equals, hashCode y toString a partir de la lista de componentes.
 * - public FormaDeAnotacion { ... }: constructor compacto del record; corre antes de guardar los campos.
 * - Objects.requireNonNull(x, "nombre"): lanza NullPointerException con ese nombre si x es null.
 * - campos = List.copyOf(campos): guarda una copia inmutable; si quien construyó la forma cambia su lista después, la
 *   forma no cambia. List.copyOf además rechaza una lista null o con elementos null.
 */
