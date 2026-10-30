package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Único advice del módulo (como el de ucotestapi). Extiende {@link ResponseEntityExceptionHandler}: así las fallas que
 * Spring ya conoce (405, 415) conservan su estado (COH-170), y el manejador de {@code Exception} solo recibe lo
 * realmente inesperado. En el Segmento R solo agrega el {@code 500} genérico del contrato (C-116); los {@code 409} y
 * {@code 503} llegan con los segmentos 1b y CR.
 */
@RestControllerAdvice(basePackages = "co.florlogic.finca.ingesta")
class ManejadorDeErrores extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorDeErrores.class);

    /**
     * La base cae a mitad o algo no previsto falla: la transacción ya se deshizo y el celular puede reenviar la
     * partición entera. Sin detalles internos en la respuesta, con la traza completa en el log.
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> inesperado(Exception fallo) {
        LOG.error("Error inesperado al recibir una partición", fallo);
        ProblemDetail problema = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problema.setType(ParticionesControlador.ERROR_INTERNO);
        problema.setTitle("Error interno");
        return ResponseEntity.internalServerError().cacheControl(CacheControl.noStore()).body(problema);
    }
}
