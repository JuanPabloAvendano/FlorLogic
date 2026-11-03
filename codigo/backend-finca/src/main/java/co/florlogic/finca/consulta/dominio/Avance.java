package co.florlogic.finca.consulta.dominio;

/**
 * Cuánto de lo esperado ya llegó (CT-13): esperado son las celdas asignadas; recibido, las que ya tienen hecho de
 * producción. Lo que falta nunca es negativo.
 */
public record Avance(int esperado, int recibido) {

    public Avance {
        if (esperado < 0 || recibido < 0 || recibido > esperado) {
            throw new IllegalArgumentException("Avance imposible: " + recibido + " de " + esperado);
        }
    }

    public int falta() {
        return esperado - recibido;
    }

    /** De 0 a 100, hacia abajo: 99,9 % todavía no es completo. Sin nada esperado, 100. */
    public int porcentaje() {
        return esperado == 0 ? 100 : (int) (100L * recibido / esperado);
    }

    public boolean completo() {
        return recibido == esperado;
    }
}
