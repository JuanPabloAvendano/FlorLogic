package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import java.net.URI;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Los errores de ingesta en {@code application/problem+json} (contrato A-22, A-36): un {@code type} por regla, sin
 * datos de negocio y con {@code Cache-Control: no-store}. Un solo lugar para los {@code type}, para no repetirlos (DRY);
 * pasan al catálogo de parámetros en la fase del catálogo (A-104, COH-200).
 */
final class Problemas {

    static final URI RECHAZADO = URI.create("https://florlogic.co/problemas/ingesta/rechazado");
    static final URI ERROR_INTERNO = URI.create("https://florlogic.co/problemas/ingesta/error-interno");
    static final URI PARTICION_EN_CURSO = URI.create("https://florlogic.co/problemas/ingesta/particion-en-curso");
    static final URI SESION_DESCONOCIDA = URI.create("https://florlogic.co/problemas/ingesta/sesion-desconocida");
    static final URI SIN_PERMISO = URI.create("https://florlogic.co/problemas/ingesta/sin-permiso");

    private Problemas() {
    }

    static ResponseEntity<ProblemDetail> rechazado() {
        return problema(HttpStatus.UNPROCESSABLE_CONTENT, RECHAZADO, "Petición rechazada", null);
    }

    static ResponseEntity<ProblemDetail> sesionDesconocida() {
        return problema(HttpStatus.NOT_FOUND, SESION_DESCONOCIDA, "Sesión desconocida", null);
    }

    static ResponseEntity<ProblemDetail> sesionAjena() {
        return problema(HttpStatus.FORBIDDEN, SIN_PERMISO, "Sin permiso", "sesion_ajena");
    }

    static ResponseEntity<ProblemDetail> problema(HttpStatus estado, URI tipo, String titulo, String regla) {
        ProblemDetail problema = ProblemDetail.forStatus(estado);
        problema.setType(tipo);
        problema.setTitle(titulo);
        if (regla != null) {
            problema.setProperty("regla", regla);
        }
        return ResponseEntity.status(estado).cacheControl(CacheControl.noStore()).body(problema);
    }
}
