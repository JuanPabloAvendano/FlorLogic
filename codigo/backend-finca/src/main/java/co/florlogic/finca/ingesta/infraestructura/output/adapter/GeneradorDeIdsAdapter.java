package co.florlogic.finca.ingesta.infraestructura.output.adapter;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.GeneradorDeIds;

/**
 * UUID v7 (RFC 9562 secc. 5.7): 48 bits de milisegundos y 74 al azar; ordenables por tiempo (TEC-12). El JDK 27 solo
 * trae v4 y v3, así que se arma aquí, sin biblioteca.
 */
@Component
class GeneradorDeIdsAdapter implements GeneradorDeIds {

    private final SecureRandom azar = new SecureRandom();
    private final Clock reloj = Clock.systemUTC();

    @Override
    public UUID nuevo() {
        long ms = reloj.millis();
        long alto = (ms << 16) | 0x7000L | (azar.nextInt() & 0x0FFFL);
        long bajo = (azar.nextLong() & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
        return new UUID(alto, bajo);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component: Spring crea una instancia y la entrega a quien pida el puerto GeneradorDeIds.
 * - SecureRandom: azar criptográfico, impredecible; los ids no se pueden adivinar.
 * - (ms << 16) | 0x7000L | (azar.nextInt() & 0x0FFFL): la mitad alta del UUID. Corre los milisegundos 16 bits a la
 *   izquierda, pone el 7 de la versión en los 4 bits que la llevan y llena los 12 bits restantes con azar (& 0x0FFF
 *   deja solo esos 12).
 * - (azar.nextLong() & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L: la mitad baja. Borra los 2 bits más altos y pone
 *   «10», la variante que exige el RFC; los otros 62 son azar.
 * - new UUID(alto, bajo): arma el UUID con sus dos mitades de 64 bits.
 * - Clock.systemUTC().millis(): milisegundos desde 1970 en UTC.
 */
