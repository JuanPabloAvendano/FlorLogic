package co.florlogic.finca.dispositivos.servicios.input.ensamblador;

import co.florlogic.finca.dispositivos.dominio.AsignacionDelDia;
import co.florlogic.finca.dispositivos.dominio.Dispositivo;
import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.AsignacionRespuestaDto;
import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.DispositivoRespuestaDto;

/** Ensamblador de salida de la web del administrador: dominio → DTO de respuesta (A-84). Sin estado. */
public final class AdministrarDispositivosEnsambladorSalida {

    private AdministrarDispositivosEnsambladorSalida() {
    }

    public static DispositivoRespuestaDto aDto(Dispositivo d) {
        return new DispositivoRespuestaDto(d.id(), d.estado().name(), d.vistoPorPrimeraVez(), d.cambiadoEn(),
                d.cambiadoPor());
    }

    public static AsignacionRespuestaDto aDto(AsignacionDelDia a) {
        return new AsignacionRespuestaDto(a.jornada(), a.dispositivo(), a.secciones(), a.campos(), a.celdas().size());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - d.estado().name(): el nombre de la constante del enum como texto («PENDIENTE», «APROBADO», «BLOQUEADO»); el DTO
 *   de respuesta lleva texto para que la web no conozca el enum del dominio.
 * - final y constructor privado: nadie puede heredar ni crear instancias; solo se usan sus métodos static.
 */
