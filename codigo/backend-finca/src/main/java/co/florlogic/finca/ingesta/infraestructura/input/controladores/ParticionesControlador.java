package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import java.security.Principal;
import java.time.Clock;
import java.util.Optional;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador.ParticionEnsamblador;
import co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador.ParticionLeidaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.ports.RecibirParticionInputPort;

/**
 * Adaptador de entrada de las particiones (plantilla endpoint T6: un controlador por caso de uso, que solo conoce el
 * puerto y sus DTO). Solo habla HTTP; la traducción del JSON del contrato es de {@link ParticionEnsamblador} (A-103).
 *
 * <p>Lee los bytes él mismo, en vez de dejar que Spring convierta el cuerpo: así un JSON roto también es un 422
 * «rechazado» y no un 400 con otro formato, y la lectura pasa por la barrera del contrato, que nunca lanza (A-57).
 */
@RestController
class ParticionesControlador {

    private final RecibirParticionInputPort recibir;
    // recibido_ms es el reloj del nodo de finca (A-45 ⟦2⟧b).
    private final Clock reloj = Clock.systemUTC();

    ParticionesControlador(RecibirParticionInputPort recibir) {
        this.recibir = recibir;
    }

    @PostMapping(path = "/ingesta/v1/sesiones/{id}/particiones", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> recibir(@PathVariable("id") String id, @RequestBody(required = false) byte[] cuerpo,
            Principal quien) {
        Optional<ParticionLeidaDto> leida = ParticionEnsamblador.aSolicitud(id, cuerpo, Personas.de(quien));
        if (leida.isEmpty()) {
            return Problemas.rechazado();
        }
        return switch (recibir.ejecutar(leida.get().solicitud())) {
            case RecibirParticionResultadoDto.Recibida r -> ResponseEntity.ok().cacheControl(CacheControl.noStore())
                    .body(ParticionEnsamblador.aRespuesta(leida.get().particionUuid(), r.respuesta(), reloj.millis()));
            case RecibirParticionResultadoDto.AnotacionInvalida a -> Problemas.rechazado();   // A-108, C-105
            case RecibirParticionResultadoDto.SesionDesconocida s -> Problemas.sesionDesconocida();
            case RecibirParticionResultadoDto.SesionAjena s -> Problemas.sesionAjena();
        };
    }
}
