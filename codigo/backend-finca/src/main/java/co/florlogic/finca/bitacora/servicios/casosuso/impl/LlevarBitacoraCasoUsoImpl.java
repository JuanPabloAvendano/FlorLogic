package co.florlogic.finca.bitacora.servicios.casosuso.impl;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.bitacora.dominio.CadenaDeHashes;
import co.florlogic.finca.bitacora.dominio.EntradaDeBitacora;
import co.florlogic.finca.bitacora.servicios.casosuso.LlevarBitacoraCasoUso;
import co.florlogic.finca.bitacora.servicios.output.dto.EntradaDto;
import co.florlogic.finca.bitacora.servicios.output.ports.repositorios.BitacoraRepositorio;

/**
 * Registra encadenando con la última entrada (con turno, para que la cadena no se bifurque) y verifica la cadena
 * completa. El momento se trunca a microsegundos, que es lo que PostgreSQL conserva: así el hash se puede recalcular.
 */
@Service
public class LlevarBitacoraCasoUsoImpl implements LlevarBitacoraCasoUso {

    private final BitacoraRepositorio repositorio;
    private final Clock reloj;

    public LlevarBitacoraCasoUsoImpl(BitacoraRepositorio repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public void registrar(String actor, String evento, String objeto, Map<String, String> detalle) {
        String anterior = repositorio.ultimoHashConTurno();
        EntradaDeBitacora e = CadenaDeHashes.encadenar(anterior == null ? CadenaDeHashes.ORIGEN : anterior,
                reloj.instant().truncatedTo(ChronoUnit.MICROS), actor, evento, objeto, detalle);
        repositorio.insertar(new EntradaDto(e.momento(), e.actor(), e.evento(), e.objeto(), e.detalle(),
                e.hashAnterior(), e.hash()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean cadenaIntegra() {
        return CadenaDeHashes.integra(repositorio.todas().stream().map(d -> new EntradaDeBitacora(d.momento(), d.actor(),
                d.evento(), d.objeto(), d.detalle(), d.hashAnterior(), d.hash())).toList());
    }
}
