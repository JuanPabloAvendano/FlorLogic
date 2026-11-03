package co.florlogic.finca.ingesta.infraestructura.output.adapter;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Component;

import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Reloj;

/** El reloj del nodo de finca en UTC (ADR-031). */
@Component
class RelojUtc implements Reloj {

    private final Clock reloj = Clock.systemUTC();

    @Override
    public Instant ahora() {
        return reloj.instant();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component: Spring crea una instancia y la entrega a quien pida el puerto Reloj.
 * - Clock.systemUTC().instant(): la hora actual del sistema como Instant, en UTC.
 */
