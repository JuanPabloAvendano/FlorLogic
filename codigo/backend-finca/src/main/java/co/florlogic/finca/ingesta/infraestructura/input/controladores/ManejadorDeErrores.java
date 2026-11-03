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

import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.ChoqueConcurrente;

/**
 * Único advice del módulo (como el de ucotestapi). Extiende {@link ResponseEntityExceptionHandler}: así las fallas que
 * Spring ya conoce (405, 415) conservan su estado (COH-170), y el manejador de {@code Exception} solo recibe lo
 * realmente inesperado. Agrega el {@code 500} genérico del contrato (C-116) y, desde el Segmento 1b.2, el {@code 409}
 * del choque entre dos particiones (A-98); el {@code 503} llega con el Segmento CR. Es la única clase de controladores
 * que ve una excepción de {@code servicios.output.ports} (COH-189, {@code CapasTest}).
 */
@RestControllerAdvice(basePackages = "co.florlogic.finca.ingesta")
class ManejadorDeErrores extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorDeErrores.class);

    /**
     * Otra partición del mismo celular ganó primero (A-98, C-112): la transacción ya se deshizo entera. El celular
     * reintenta tras {@code Retry-After}; lo que la otra guardó le saldrá {@code sin_efecto}.
     */
    @ExceptionHandler(ChoqueConcurrente.class)
    ResponseEntity<ProblemDetail> choque(ChoqueConcurrente choque) {
        LOG.info("Partición en curso (409): {}", choque.getMessage());
        ProblemDetail problema = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problema.setType(Problemas.PARTICION_EN_CURSO);
        problema.setTitle("Partición en curso");
        return ResponseEntity.status(HttpStatus.CONFLICT).header("Retry-After", "1").cacheControl(CacheControl.noStore())
                .body(problema);
    }

    /**
     * La base cae a mitad o algo no previsto falla: la transacción ya se deshizo y el celular puede reenviar la
     * partición entera. Sin detalles internos en la respuesta, con la traza completa en el log.
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> inesperado(Exception fallo) {
        LOG.error("Error inesperado al recibir una partición", fallo);
        ProblemDetail problema = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problema.setType(Problemas.ERROR_INTERNO);
        problema.setTitle("Error interno");
        return ResponseEntity.internalServerError().cacheControl(CacheControl.noStore()).body(problema);
    }
}
