package co.florlogic.finca.ciclo.servicios.input.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.florlogic.finca.ciclo.servicios.casosuso.ResolverTrabajoCasoUso;
import co.florlogic.finca.ciclo.servicios.input.dto.respuesta.TrabajoRespuestaDto;
import co.florlogic.finca.ciclo.servicios.input.ports.TrabajosDelGerenteInputPort;

/** Adaptador de entrada de la cola del gerente: solo delega y copia a los DTO de respuesta (A-84). */
@Service
public class TrabajosDelGerenteInputAdapter implements TrabajosDelGerenteInputPort {

    private final ResolverTrabajoCasoUso casoUso;

    public TrabajosDelGerenteInputAdapter(ResolverTrabajoCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public List<TrabajoRespuestaDto> abiertos() {
        return casoUso.abiertos().stream().map(t -> new TrabajoRespuestaDto(t.anotacion(), t.dispositivo(), t.seccionId(),
                t.jornada(), t.codigos(), t.abiertoEn())).toList();
    }

    @Override
    public boolean resolver(UUID anotacion, String gerente) {
        return casoUso.resolver(anotacion, gerente);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): Spring crea una instancia y la entrega a quien pida TrabajosDelGerenteInputPort (el
 *   controlador web).
 * - casoUso.abiertos().stream().map(t → new TrabajoRespuestaDto(…)).toList(): Stream de Java; recorre la lista,
 *   copia cada TrabajoDto de salida en un DTO de respuesta y devuelve una lista nueva que no se puede modificar.
 */
