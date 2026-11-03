package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

/** Lo que devuelve el puerto de cierre, con nombre (A-57). */
public sealed interface CerrarSesionResultadoDto {

    record Cerrada(SesionCerradaRespuestaDto respuesta) implements CerrarSesionResultadoDto {
    }

    record SesionDesconocida() implements CerrarSesionResultadoDto {
    }

    record SesionAjena() implements CerrarSesionResultadoDto {
    }
}
