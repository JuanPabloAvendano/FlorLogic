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

    static ResponseEntity<ProblemDetail> dispositivoBloqueado() {
        return problema(HttpStatus.FORBIDDEN, SIN_PERMISO, "Sin permiso", "dispositivo_bloqueado");
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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - URI.create("https://florlogic.co/…"): el type de cada problema; es un identificador, no una página que se
 *   visite.
 * - HttpStatus.UNPROCESSABLE_CONTENT / NOT_FOUND / FORBIDDEN: los estados 422, 404 y 403.
 * - ProblemDetail.forStatus(...) + setType / setTitle / setProperty("regla", …): el cuerpo de error RFC 9457;
 *   setProperty agrega el campo extra regla al JSON (sesion_ajena, dispositivo_bloqueado).
 * - ResponseEntity.status(estado).cacheControl(CacheControl.noStore()).body(problema): arma la respuesta con
 *   Cache-Control: no-store; Spring la envía como application/problem+json.
 */
