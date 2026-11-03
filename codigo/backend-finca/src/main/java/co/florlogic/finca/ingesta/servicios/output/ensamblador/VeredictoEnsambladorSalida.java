package co.florlogic.finca.ingesta.servicios.output.ensamblador;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import co.florlogic.finca.ingesta.dominio.comun.CodigoMarca;
import co.florlogic.finca.ingesta.dominio.comun.MotivoRechazo;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.Veredicto;
import co.florlogic.finca.ingesta.servicios.output.dto.ContenidoAEvaluarDto;
import co.florlogic.finca.ingesta.servicios.output.dto.VeredictoDto;

/**
 * Ensamblador de salida hacia {@code reglas}: la anotación → lo que se evalúa, y lo que responde → {@link Veredicto}
 * (A-84, A-99). Un código que ingesta no conoce no se inventa: un motivo desconocido es {@code DATOS_INVALIDOS} y
 * una marca desconocida se ignora, así un {@code reglas} más nuevo no tumba la recepción (A-57).
 */
public final class VeredictoEnsambladorSalida {

    private VeredictoEnsambladorSalida() {
    }

    public static ContenidoAEvaluarDto aDto(AnotacionRecibida anotacion) {
        return new ContenidoAEvaluarDto(anotacion.tipo(), anotacion.versionTipo(), anotacion.marcadaEnElCelular(),
                anotacion.contenidoJson());
    }

    public static Veredicto aDominio(VeredictoDto veredicto) {
        Optional<String> motivo = veredicto.motivoRechazo();
        if (motivo.isPresent()) {
            return Veredicto.rechazo(enumDe(MotivoRechazo.class, motivo.get()).orElse(MotivoRechazo.DATOS_INVALIDOS));
        }
        Set<CodigoMarca> marcas = EnumSet.noneOf(CodigoMarca.class);
        for (String codigo : veredicto.marcas()) {
            enumDe(CodigoMarca.class, codigo).ifPresent(marcas::add);
        }
        return Veredicto.marca(marcas);
    }

    /** El motivo guardado (en minúscula) como dominio; uno desconocido es {@code DATOS_INVALIDOS}. */
    public static MotivoRechazo motivoDe(String codigo) {
        return enumDe(MotivoRechazo.class, codigo).orElse(MotivoRechazo.DATOS_INVALIDOS);
    }

    private static <E extends Enum<E>> Optional<E> enumDe(Class<E> tipo, String codigo) {
        try {
            return Optional.of(Enum.valueOf(tipo, codigo.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
