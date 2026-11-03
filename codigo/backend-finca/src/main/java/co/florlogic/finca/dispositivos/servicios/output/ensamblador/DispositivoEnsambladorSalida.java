package co.florlogic.finca.dispositivos.servicios.output.ensamblador;

import co.florlogic.finca.dispositivos.dominio.Dispositivo;
import co.florlogic.finca.dispositivos.servicios.output.dto.DispositivoDto;

/** Ensamblador de salida: agregado ↔ DTO (A-84). Sin estado. */
public final class DispositivoEnsambladorSalida {

    private DispositivoEnsambladorSalida() {
    }

    public static DispositivoDto aDto(Dispositivo d) {
        return new DispositivoDto(d.id(), d.estado().name(), d.vistoPorPrimeraVez(), d.cambiadoEn(), d.cambiadoPor());
    }

    public static Dispositivo aDominio(DispositivoDto d) {
        return Dispositivo.desde(d.id(), d.vistoPorPrimeraVez(), Dispositivo.Estado.valueOf(d.estado()), d.cambiadoEn(),
                d.cambiadoPor());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - d.estado().name() y Dispositivo.Estado.valueOf(d.estado()): ida y vuelta entre el enum del dominio y su nombre en
 *   texto. valueOf lanza IllegalArgumentException si el texto no es una de las tres constantes; la restricción CHECK
 *   de la tabla dispositivo impide que la base guarde otro.
 * - final y constructor privado: nadie puede heredar ni crear instancias; solo se usan sus métodos static.
 */
