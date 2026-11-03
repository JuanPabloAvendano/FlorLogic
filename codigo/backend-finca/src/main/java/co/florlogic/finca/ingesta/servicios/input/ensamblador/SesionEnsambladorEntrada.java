package co.florlogic.finca.ingesta.servicios.input.ensamblador;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.dominio.sesion.ResultadoDeApertura;
import co.florlogic.finca.ingesta.dominio.sesion.ResultadoDeCierre;
import co.florlogic.finca.ingesta.dominio.sesion.SesionAbiertaValor;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.AbrirSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.CerrarSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.SesionAbiertaRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.SesionCerradaRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.AbrirSesionSolicitudDto;

/** Ensamblador de entrada de la sesión: solicitud → dominio y dominio → respuesta (A-84). Sin estado. */
public final class SesionEnsambladorEntrada {

    private SesionEnsambladorEntrada() {
    }

    public static Optional<DispositivoId> dispositivo(AbrirSesionSolicitudDto solicitud) {
        return DispositivoId.de(solicitud.dispositivoId());
    }

    public static Instant relojDelCelular(AbrirSesionSolicitudDto solicitud) {
        return Instant.ofEpochMilli(solicitud.relojMs());
    }

    public static SesionAbiertaRespuestaDto aRespuesta(SesionAbiertaValor abierta) {
        return new SesionAbiertaRespuestaDto(abierta.sesion().id().valor(), abierta.sesion().desfaseMs(),
                abierta.confirmadoHasta(), abierta.sesion().versionConfig(), !abierta.sesion().dispositivoRegistrado());
    }

    public static AbrirSesionResultadoDto aResultado(ResultadoDeApertura resultado) {
        return switch (resultado) {
            case ResultadoDeApertura.Abierta a -> new AbrirSesionResultadoDto.Abierta(aRespuesta(a.abierta()));
            case ResultadoDeApertura.DispositivoBloqueado b -> new AbrirSesionResultadoDto.DispositivoBloqueado();
        };
    }

    public static CerrarSesionResultadoDto aResultado(ResultadoDeCierre resultado) {
        return switch (resultado) {
            case ResultadoDeCierre.Cerrada c -> new CerrarSesionResultadoDto.Cerrada(new SesionCerradaRespuestaDto(
                    c.sesion().id().valor(), c.sesion().estado().name().toLowerCase(Locale.ROOT), c.confirmadoHasta(),
                    c.sesion().versionConfig(), List.of()));
            case ResultadoDeCierre.SesionDesconocida s -> new CerrarSesionResultadoDto.SesionDesconocida();
            case ResultadoDeCierre.SesionAjena s -> new CerrarSesionResultadoDto.SesionAjena();
        };
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Instant.ofEpochMilli(solicitud.relojMs()): convierte los milisegundos desde 1970 del celular en un Instant.
 * - return switch (resultado) { case … -> … }: switch con patrones sobre las interfaces selladas de apertura y
 *   cierre; sin default.
 * - name().toLowerCase(Locale.ROOT): el estado del enum en minúscula (cerrada, parcial) sin depender del idioma del
 *   sistema.
 * - List.of(): lista vacía no modificable (avisos, por ahora ninguno).
 */
