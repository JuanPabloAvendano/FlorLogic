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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: esperado(), recibido()), equals, hashCode y toString a partir de la lista de componentes.
 * - public Avance { ... }: constructor compacto; rechaza un avance imposible con IllegalArgumentException antes de
 *   crearlo, así ningún Avance tiene «falta» negativo.
 * - (int) (100L * recibido / esperado): multiplica en long (la L de 100L) para no desbordar int con conteos grandes; la
 *   división entera corta los decimales, que es lo que hace que 999 de 1000 dé 99 y no 100. El (int) vuelve al tipo
 *   de retorno.
 * - esperado == 0 ? 100 : ...: operador ternario; evita dividir por cero.
 */
