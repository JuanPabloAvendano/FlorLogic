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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): registra la clase como bean; Spring la crea una sola vez y le pasa por el constructor lo que pide (inyección por constructor, sin @Autowired).
 * - Clock: reloj del JDK que llega como bean (RelojDelNodo) en vez de llamar Instant.now(); las pruebas pueden fijarlo.
 * - truncatedTo(ChronoUnit.MICROS): corta el Instant a microsegundos (el porqué está en el Javadoc).
 * - @Transactional en registrar: si quien llama ya tiene transacción, se une a ella (propagación por defecto); si no,
 *   abre una. Por eso la entrada se deshace junto con la operación que se registra, y el candado de turno que toma
 *   ultimoHashConTurno dura hasta el fin de esa misma transacción.
 * - @Transactional(readOnly = true) en cadenaIntegra: transacción de solo lectura para releer la cadena completa.
 * - anterior == null ? ORIGEN : anterior: operador ternario; la bitácora vacía encadena con el origen.
 * - stream().map(d -> ...).toList(): convierte cada fila (DTO) en una entrada del dominio y las junta en una lista.
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
