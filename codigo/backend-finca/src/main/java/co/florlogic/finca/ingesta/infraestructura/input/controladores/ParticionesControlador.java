package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import java.net.URI;
import java.time.Clock;
import java.util.Optional;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador.ParticionEnsamblador;
import co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador.ParticionLeidaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.ports.RecibirParticionInputPort;

/**
 * Adaptador de entrada del camino mínimo de TX-1 (D-17; plantilla endpoint T6: un controlador por caso de uso, que
 * solo conoce el puerto y sus DTO; nunca el dominio). Solo habla HTTP: la traducción del contrato es de
 * {@link ParticionEnsamblador} (A-103), el único que conoce el contrato y Jackson.
 *
 * <p>Lee los bytes él mismo, en vez de dejar que Spring convierta el cuerpo: así un JSON roto también es un 422
 * «rechazado» y no un 400 con otro formato, y la lectura pasa por la barrera del contrato, que nunca lanza (A-57).
 */
@RestController
class ParticionesControlador {

    // Pasan al catálogo de parámetros en el Segmento CR (A-104, COH-200); el contrato los nombra, no se cambian a mano.
    static final URI RECHAZADO = URI.create("https://florlogic.co/problemas/ingesta/rechazado");
    /** Lo inesperado (C-116): lo responde {@link ManejadorDeErrores}, sin detalles internos. */
    static final URI ERROR_INTERNO = URI.create("https://florlogic.co/problemas/ingesta/error-interno");
    /** Dos particiones del mismo celular a la vez (A-98): lo responde {@link ManejadorDeErrores} con {@code Retry-After}. */
    static final URI PARTICION_EN_CURSO = URI.create("https://florlogic.co/problemas/ingesta/particion-en-curso");

    private final RecibirParticionInputPort recibir;
    // recibido_ms es el reloj del nodo de finca (A-45 ⟦2⟧b).
    private final Clock reloj = Clock.systemUTC();

    ParticionesControlador(RecibirParticionInputPort recibir) {
        this.recibir = recibir;
    }

    @PostMapping(path = "/ingesta/v1/sesiones/{id}/particiones", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> recibir(@PathVariable("id") String id, @RequestBody(required = false) byte[] cuerpo) {
        Optional<ParticionLeidaDto> leida = ParticionEnsamblador.aSolicitud(id, cuerpo);
        if (leida.isEmpty()) {
            return rechazado();
        }
        Optional<RecibirParticionRespuestaDto> resultado = recibir.ejecutar(leida.get().solicitud());
        if (resultado.isEmpty()) {
            return rechazado();   // el dominio rechazó una anotación (A-108, C-105)
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(ParticionEnsamblador.aRespuesta(leida.get().particionUuid(), resultado.get(), reloj.millis()));
    }

    private static ResponseEntity<ProblemDetail> rechazado() {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, null);
        problema.setType(RECHAZADO);
        problema.setTitle("Partición rechazada");
        return ResponseEntity.unprocessableContent().cacheControl(CacheControl.noStore()).body(problema);
    }
}
