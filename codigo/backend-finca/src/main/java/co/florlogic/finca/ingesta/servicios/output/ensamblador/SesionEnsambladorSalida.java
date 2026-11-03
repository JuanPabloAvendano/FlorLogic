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
                sesion.desfaseMs(), sesion.versionConfig(), sesion.dispositivoRegistrado(), sesion.estado().name(),
                sesion.cerradaEn().orElse(null));
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
                sesion.desfaseMs(), sesion.versionConfig(), sesion.dispositivoRegistrado(), e, sesion.cerradaEn()));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - EstadoSesion.valueOf(sesion.estado()): el texto guardado vuelto enum; lanza IllegalArgumentException si no
 *   existe y NullPointerException si es null, por eso el catch con | atrapa las dos.
 * - estado.flatMap(e -> SesionDeSincronizacion.desde(...)): si el estado sirvió, intenta rehacer la sesión, que
 *   también puede dar vacío.
 * - DispositivoId.de(...).orElse(null): un dispositivo ausente pasa como null y desde(...) lo vuelve vacío.
 * - sesion.cerradaEn().orElse(null): el Optional del dominio pasa a null en el DTO.
 */
