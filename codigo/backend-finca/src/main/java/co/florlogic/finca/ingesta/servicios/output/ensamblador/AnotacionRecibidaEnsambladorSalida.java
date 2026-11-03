package co.florlogic.finca.ingesta.servicios.output.ensamblador;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.JuicioDeAnotacion;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRechazadaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDto;

/**
 * Ensamblador de salida de las anotaciones: dominio + juicio → el DTO que cruza el puerto (Fowler · PoEAA secc. 100,
 * Assembler; A-84). Los códigos van en minúscula, como en el contrato. Sin estado, por eso son métodos estáticos.
 */
public final class AnotacionRecibidaEnsambladorSalida {

    private AnotacionRecibidaEnsambladorSalida() {
    }

    public static AnotacionRecibidaDto aAceptada(AnotacionRecibida anotacion, UUID sesionId, String huella,
            JuicioDeAnotacion juicio) {
        List<String> codigos = juicio.codigos().stream().map(c -> minuscula(c.name())).sorted().toList();
        return new AnotacionRecibidaDto(anotacion.anotacionId().valor(), anotacion.consecutivo().valor(), sesionId,
                anotacion.tipo(), anotacion.versionTipo(), huella, juicio.resultado().name(), codigos,
                anotacion.contenidoJson());
    }

    public static AnotacionRechazadaDto aRechazada(AnotacionRecibida anotacion, UUID sesionId, String huella,
            JuicioDeAnotacion juicio) {
        String motivo = juicio.motivo().map(m -> minuscula(m.name())).orElseThrow();
        return new AnotacionRechazadaDto(anotacion.anotacionId().valor(), huella, sesionId,
                anotacion.consecutivo().valor(), motivo, anotacion.contenidoJson());
    }

    static String minuscula(String nombre) {
        return nombre.toLowerCase(Locale.ROOT);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - juicio.codigos().stream().map(...).sorted().toList(): los códigos en minúscula, en orden alfabético, como lista
 *   no modificable.
 * - juicio.motivo().map(...).orElseThrow(): si un rechazo llegara sin motivo, lanza NoSuchElementException; sería
 *   un error de programación.
 * - toLowerCase(Locale.ROOT): minúsculas sin depender del idioma del sistema.
 */
