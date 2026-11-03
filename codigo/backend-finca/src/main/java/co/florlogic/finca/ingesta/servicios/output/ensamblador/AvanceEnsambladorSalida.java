package co.florlogic.finca.ingesta.servicios.output.ensamblador;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import co.florlogic.finca.ingesta.dominio.avance.AvanceDelDispositivoAgregado;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.servicios.output.dto.AvanceDto;

/**
 * Ensamblador de salida del avance: solo traduce, agregado ↔ DTO (Fowler · PoEAA secc. 100, Assembler; A-84). Sin
 * estado, por eso son métodos estáticos y no un bean, como {@code AnotacionRecibidaEnsambladorSalida}.
 */
public final class AvanceEnsambladorSalida {

    private AvanceEnsambladorSalida() {
    }

    public static AvanceDto aDto(AvanceDelDispositivoAgregado avance) {
        return new AvanceDto(
                avance.dispositivo().valor(),
                avance.confirmadoHasta().map(ConsecutivoValor::valor).orElse(0L),
                avance.porEncima().stream().map(ConsecutivoValor::valor).toList(),
                avance.version());
    }

    /**
     * Rehace el agregado con lo guardado, sin juzgar (A-81 ⟦2⟧). Vacío, sin lanzar, si lo guardado no es un avance
     * válido: un número negativo o que rompe INV-4. Qué hace el caso de uso con ese vacío: COH-131.
     */
    public static Optional<AvanceDelDispositivoAgregado> aDominio(AvanceDto avance) {
        Optional<ConsecutivoValor> confirmado = avance.confirmadoHasta() == 0
                ? Optional.empty()
                : ConsecutivoValor.de(avance.confirmadoHasta());
        if (avance.confirmadoHasta() != 0 && confirmado.isEmpty()) {
            return Optional.empty();
        }
        SortedSet<ConsecutivoValor> porEncima = new TreeSet<>();
        for (Long numero : avance.porEncima()) {
            Optional<ConsecutivoValor> valor = numero == null ? Optional.empty() : ConsecutivoValor.de(numero);
            if (valor.isEmpty()) {
                return Optional.empty();
            }
            porEncima.add(valor.get());
        }
        return AvanceDelDispositivoAgregado.desde(new DispositivoId(avance.dispositivoId()), confirmado, porEncima,
                avance.version());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - avance.confirmadoHasta().map(ConsecutivoValor::valor).orElse(0L): el Optional del dominio pasa a 0 en el DTO.
 * - avance.porEncima().stream().map(ConsecutivoValor::valor).toList(): los números por encima como lista de Long.
 * - SortedSet / TreeSet: conjunto ordenado que exige desde(...) del agregado.
 * - Optional en aDominio: vacío, sin lanzar, ante un número negativo, un null o un avance que rompe la regla.
 */
