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
