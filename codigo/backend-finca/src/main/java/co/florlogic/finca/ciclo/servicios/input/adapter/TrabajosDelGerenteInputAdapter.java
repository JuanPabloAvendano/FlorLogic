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
