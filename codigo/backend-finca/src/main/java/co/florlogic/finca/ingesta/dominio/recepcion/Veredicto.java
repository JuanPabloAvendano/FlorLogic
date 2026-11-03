package co.florlogic.finca.ingesta.dominio.recepcion;

import java.util.Objects;
import java.util.Set;

import co.florlogic.finca.ingesta.dominio.comun.CodigoMarca;
import co.florlogic.finca.ingesta.dominio.comun.MotivoRechazo;

/**
 * Lo que dicen las reglas del contenido de una anotación nueva (ESPEC-dominio-ingesta §5, A-99, A-101): nada que
 * marcar, una marca con uno o más códigos, o un rechazo con un solo motivo. Lo traduce el adaptador del módulo
 * {@code reglas}; ingesta no sabe cómo se juzgó.
 */
public sealed interface Veredicto {

    Veredicto SIN_MARCA = new SinMarca();

    static Veredicto marca(Set<CodigoMarca> codigos) {
        return codigos.isEmpty() ? SIN_MARCA : new Marca(Set.copyOf(codigos));
    }

    static Veredicto rechazo(MotivoRechazo motivo) {
        return new Rechazo(Objects.requireNonNull(motivo, "motivo"));
    }

    record SinMarca() implements Veredicto {
    }

    record Marca(Set<CodigoMarca> codigos) implements Veredicto {
    }

    record Rechazo(MotivoRechazo motivo) implements Veredicto {
    }
}
