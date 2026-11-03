package co.florlogic.finca.ingesta.dominio.recepcion;

import java.util.Objects;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;

/**
 * Una anotación que llegó del celular, todavía sin juicio. Es dominio: Java puro, sin Spring y sin el módulo
 * {@code contrato}. El ensamblador del controlador traduce el JSON a la solicitud del puerto y el ensamblador de
 * entrada traduce la solicitud a este record; un id que no es v7 o un consecutivo menor que 1 no llegan a existir (A-108).
 *
 * @param tipo y {@code versionTipo}: con qué forma del catálogo se juzga (A-87, A-101)
 * @param marcadaEnElCelular el veredicto local fue {@code marcada}; si el nodo juzga distinto, es divergencia (contrato A-32)
 * @param contenidoJson la anotación completa en JSON equivalente (A-59): de aquí sale la huella y lo leen las reglas
 */
public record AnotacionRecibida(AnotacionId anotacionId, ConsecutivoValor consecutivo, String tipo, int versionTipo,
        boolean marcadaEnElCelular, String contenidoJson) {

    public AnotacionRecibida {
        Objects.requireNonNull(anotacionId, "anotacionId");
        Objects.requireNonNull(consecutivo, "consecutivo");
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(contenidoJson, "contenidoJson");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto: Objects.requireNonNull lanza NullPointerException si falta id, consecutivo,
 *   tipo o contenido; los ensambladores garantizan que no pase.
 */
