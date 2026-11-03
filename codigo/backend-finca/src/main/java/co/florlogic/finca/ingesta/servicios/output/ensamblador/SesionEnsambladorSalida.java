package co.florlogic.finca.ingesta.servicios.output.ensamblador;

import java.util.Optional;

import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.dominio.sesion.EstadoSesion;
import co.florlogic.finca.ingesta.dominio.sesion.SesionDeSincronizacion;
import co.florlogic.finca.ingesta.dominio.sesion.SesionId;
import co.florlogic.finca.ingesta.servicios.output.dto.SesionDto;

/** Ensamblador de salida de la sesión: agregado ↔ DTO (A-84). Lo guardado incoherente sale vacío, sin lanzar. */
public final class SesionEnsambladorSalida {

    private SesionEnsambladorSalida() {
    }

    public static SesionDto aDto(SesionDeSincronizacion sesion) {
        return new SesionDto(sesion.id().valor(), sesion.dispositivo().valor(), sesion.persona(), sesion.abiertaEn(),
                sesion.desfaseMs(), sesion.versionConfig(), sesion.estado().name(), sesion.cerradaEn().orElse(null));
    }

    public static Optional<SesionDeSincronizacion> aDominio(SesionDto sesion) {
        Optional<EstadoSesion> estado;
        try {
            estado = Optional.of(EstadoSesion.valueOf(sesion.estado()));
        } catch (IllegalArgumentException | NullPointerException e) {
            estado = Optional.empty();
        }
        return estado.flatMap(e -> SesionDeSincronizacion.desde(new SesionId(sesion.sesionId()),
                DispositivoId.de(sesion.dispositivoId()).orElse(null), sesion.persona(), sesion.abiertaEn(),
                sesion.desfaseMs(), sesion.versionConfig(), e, sesion.cerradaEn()));
    }
}
