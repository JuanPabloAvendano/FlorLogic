package co.florlogic.finca.ingesta.dominio.recepcion;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.CodigoMarca;
import co.florlogic.finca.ingesta.dominio.comun.MotivoRechazo;
import co.florlogic.finca.ingesta.dominio.comun.Resultado;

/**
 * El juicio de una anotación de la partición: su resultado y por qué (contrato A-23). Una {@code MARCADA} lleva sus
 * códigos; una {@code RECHAZADA}, un solo motivo.
 */
public record JuicioDeAnotacion(AnotacionId anotacion, Resultado resultado, Set<CodigoMarca> codigos,
        Optional<MotivoRechazo> motivo) {

    public JuicioDeAnotacion {
        Objects.requireNonNull(anotacion, "anotacion");
        Objects.requireNonNull(resultado, "resultado");
        codigos = Set.copyOf(codigos);
        Objects.requireNonNull(motivo, "motivo");
    }

    public static JuicioDeAnotacion aplicada(AnotacionId anotacion, Set<CodigoMarca> codigos) {
        return new JuicioDeAnotacion(anotacion, codigos.isEmpty() ? Resultado.APLICADA : Resultado.MARCADA, codigos,
                Optional.empty());
    }

    public static JuicioDeAnotacion rechazada(AnotacionId anotacion, MotivoRechazo motivo) {
        return new JuicioDeAnotacion(anotacion, Resultado.RECHAZADA, Set.of(), Optional.of(motivo));
    }

    public static JuicioDeAnotacion sinEfecto(AnotacionId anotacion) {
        return new JuicioDeAnotacion(anotacion, Resultado.SIN_EFECTO, Set.of(), Optional.empty());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto: Set.copyOf(codigos) deja una copia no modificable y sin null.
 * - Optional<MotivoRechazo>: solo una RECHAZADA lleva motivo; las demás llevan Optional.empty().
 * - aplicada / rechazada / sinEfecto: métodos de fábrica estáticos; aplicada elige APLICADA o MARCADA con el
 *   operador ternario según haya códigos.
 */
