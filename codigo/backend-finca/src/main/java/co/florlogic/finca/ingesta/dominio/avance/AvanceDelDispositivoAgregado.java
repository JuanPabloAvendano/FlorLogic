package co.florlogic.finca.ingesta.dominio.avance;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;

/**
 * Raíz de agregado: cuánto ha recibido el nodo de finca de un celular (ESPEC-dominio-ingesta A-60). Protege
 * <b>INV-4</b>: {@code confirmadoHasta} es el mayor c con 1..c recibidos, y nunca baja. Con eso el celular sabe qué
 * puede borrar (A-31): nunca algo que el nodo no tiene.
 *
 * <p>Los números que llegan por encima de un hueco se guardan aparte ({@code porEncima}); cuando llega lo que faltaba,
 * {@code confirmadoHasta} salta hasta el último contiguo (A-72). {@code porEncima} es una ventana con tope
 * ({@link #TOPE_POR_ENCIMA}, VER-6): un celular con un hueco temprano no puede llenar la memoria. Se referencia por
 * {@link DispositivoId} y nunca contiene anotaciones: crecería sin límite (par 4b de A-60).
 *
 * <p>Nace con {@link #nuevo} y se carga con {@link #desde}; constructor privado (E-13). Es igual a otro por su
 * dispositivo (E-09). La {@code version} es para la concurrencia optimista: la sube el adaptador al guardar, no el
 * dominio (DI1-1, E-15).
 */
public final class AvanceDelDispositivoAgregado {

    /**
     * Cuántos números por encima de un hueco se recuerdan como máximo (VER-6, Juan 29-oct: ventana). Fijo en el
     * dominio hasta que exista el catálogo de configuración; el que no cabe no se pierde (A-72).
     */
    public static final int TOPE_POR_ENCIMA = 10_000;

    private final DispositivoId dispositivo;
    // 0 = nada recibido (A-78). Un long y no un Optional como campo: Optional es para lo que se devuelve (VER-7).
    private long confirmado;
    private final TreeSet<ConsecutivoValor> porEncima;
    private final long version;

    private AvanceDelDispositivoAgregado(DispositivoId dispositivo, Optional<ConsecutivoValor> confirmadoHasta,
            SortedSet<ConsecutivoValor> porEncima, long version) {
        this.dispositivo = Objects.requireNonNull(dispositivo, "dispositivo");
        this.confirmado = valorDe(Objects.requireNonNull(confirmadoHasta, "confirmadoHasta"));
        // Copia propia: lo que tenga quien llama no puede cambiar el avance (E-16). Con orden natural, no con el de
        // quien llama: new TreeSet<>(SortedSet) hereda su comparador, y con otro orden first() ya no sería el menor y
        // registrar nunca absorbería (COH-142).
        this.porEncima = new TreeSet<>();
        this.porEncima.addAll(porEncima);
        this.version = version;
    }

    /** Un celular del que no se ha recibido nada: {@code confirmadoHasta} vacío (A-78) y versión 0 (A-81). */
    public static AvanceDelDispositivoAgregado nuevo(DispositivoId dispositivo) {
        return new AvanceDelDispositivoAgregado(dispositivo, Optional.empty(), new TreeSet<>(), 0L);
    }

    /**
     * Carga un avance guardado, sin juzgar ni crear eventos (A-81 ⟦2⟧). Da vacío, sin lanzar, si algún argumento es
     * {@code null}, si la versión es negativa (nace en 0, A-81; VER-7), si {@code porEncima} pasa del tope (VER-6) o si
     * lo guardado rompe INV-4: un número de {@code porEncima} que ya debió subir {@code confirmadoHasta}
     * (≤ confirmado + 1; con confirmado vacío, todos deben ser ≥ 2, COH-130). Qué hace el servicio con ese vacío es de
     * la semilla caso de uso (COH-131).
     */
    public static Optional<AvanceDelDispositivoAgregado> desde(DispositivoId dispositivo,
            Optional<ConsecutivoValor> confirmadoHasta, SortedSet<ConsecutivoValor> porEncima, long version) {
        if (dispositivo == null || confirmadoHasta == null || porEncima == null || version < 0
                || porEncima.size() > TOPE_POR_ENCIMA) {
            return Optional.empty();
        }
        long base = valorDe(confirmadoHasta);
        for (ConsecutivoValor numero : porEncima) {
            // numero - 1 no se desborda (vale 1 o más); base + 1 sí podría.
            if (numero == null || numero.valor() - 1 <= base) {
                return Optional.empty();
            }
        }
        return Optional.of(new AvanceDelDispositivoAgregado(dispositivo, confirmadoHasta, porEncima, version));
    }

    /**
     * Anota que llegó este consecutivo (A-73: toda llegada con consecutivo válido, también un reenvío o una
     * corrupta). Idempotente (C-91, DI1-8): uno que ya está confirmado o ya está en {@code porEncima} no cambia nada.
     * Uno contiguo sube {@code confirmadoHasta} y absorbe los de {@code porEncima} que queden contiguos; uno que deja
     * un hueco se guarda en {@code porEncima} y {@code confirmadoHasta} no se mueve (A-72), salvo que la ventana esté
     * llena (VER-6).
     *
     * @param consecutivo ya construido por el caso de uso; {@code null} es un error de programación
     * @return qué pasó con él, con nombre (A-57)
     */
    public LlegadaAlAvance registrar(ConsecutivoValor consecutivo) {
        Objects.requireNonNull(consecutivo, "consecutivo");
        if (consecutivo.valor() <= confirmado) {
            return LlegadaAlAvance.YA_CUBIERTO;
        }
        // Se compara con valor - 1 y no con siguiente(): así un consecutivo en Long.MAX_VALUE no se desborda.
        if (consecutivo.valor() - 1 != confirmado) {
            if (porEncima.contains(consecutivo)) {
                return LlegadaAlAvance.EN_ESPERA;
            }
            if (porEncima.size() >= TOPE_POR_ENCIMA) {
                return LlegadaAlAvance.FUERA_DE_VENTANA;
            }
            porEncima.add(consecutivo);
            return LlegadaAlAvance.EN_ESPERA;
        }
        ConsecutivoValor ultimo = consecutivo;
        while (!porEncima.isEmpty() && porEncima.first().valor() - 1 == ultimo.valor()) {
            ultimo = porEncima.pollFirst();
        }
        confirmado = ultimo.valor();
        return LlegadaAlAvance.AVANZA;
    }

    public DispositivoId dispositivo() {
        return dispositivo;
    }

    /** El mayor consecutivo recibido sin huecos por debajo; vacío si no se ha recibido nada (A-78; 0 en el cable). */
    public Optional<ConsecutivoValor> confirmadoHasta() {
        return ConsecutivoValor.de(confirmado);
    }

    /** Lo recibido por encima de un hueco, en orden. Vista no modificable (DI1-4, E-16). */
    public SortedSet<ConsecutivoValor> porEncima() {
        return Collections.unmodifiableSortedSet(porEncima);
    }

    public long version() {
        return version;
    }

    // Vacío vale 0: así «el siguiente de nada es 1» (A-78) sale de la misma cuenta que los demás.
    private static long valorDe(Optional<ConsecutivoValor> confirmadoHasta) {
        return confirmadoHasta.map(ConsecutivoValor::valor).orElse(0L);
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof AvanceDelDispositivoAgregado avance && dispositivo.equals(avance.dispositivo);
    }

    @Override
    public int hashCode() {
        return dispositivo.hashCode();
    }

    @Override
    public String toString() {
        return "AvanceDelDispositivoAgregado[" + dispositivo.valor() + ", confirmadoHasta="
                + confirmado + ", porEncima=" + porEncima.size() + "]";
    }
}
