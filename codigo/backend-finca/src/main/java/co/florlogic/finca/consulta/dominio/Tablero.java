package co.florlogic.finca.consulta.dominio;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * El tablero de una jornada (CT-13): por celular y por sección, cuánto se esperaba, cuánto llegó y cuánto falta, más lo
 * que pide atención (marcadas, rechazadas, sesiones parciales).
 */
public record Tablero(LocalDate jornada, List<PorCelular> porCelular, List<PorSeccion> porSeccion) {

    public record PorCelular(UUID dispositivo, Avance avance, int marcadas, int rechazadas, int parciales) {

        public PorCelular {
            Objects.requireNonNull(dispositivo, "dispositivo");
            Objects.requireNonNull(avance, "avance");
        }

        /** Algo que el gerente debe mirar aunque el avance vaya bien. */
        public boolean pideAtencion() {
            return marcadas > 0 || rechazadas > 0 || parciales > 0;
        }
    }

    public record PorSeccion(String seccion, Avance avance, int marcadas) {

        public PorSeccion {
            Objects.requireNonNull(seccion, "seccion");
            Objects.requireNonNull(avance, "avance");
        }
    }

    public Tablero {
        Objects.requireNonNull(jornada, "jornada");
        porCelular = List.copyOf(porCelular);
        porSeccion = List.copyOf(porSeccion);
    }

    /** La suma de todos los celulares: lo que la finca esperaba hoy y lo que llegó. */
    public Avance total() {
        return new Avance(porCelular.stream().mapToInt(c -> c.avance().esperado()).sum(),
                porCelular.stream().mapToInt(c -> c.avance().recibido()).sum());
    }
}
