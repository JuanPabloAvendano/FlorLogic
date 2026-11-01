package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

/**
 * Otra partición del mismo celular se guardó entre la lectura y la escritura de esta (A-98). Vive junto al puerto que
 * la lanza (A-97): el caso de uso no la atrapa, así la transacción entera se deshace. El {@code 409} con
 * {@code Retry-After} llega en el Segmento 1b.2; hasta entonces sale como el {@code 500} genérico.
 */
public class ChoqueConcurrente extends RuntimeException {

    public ChoqueConcurrente(String mensaje) {
        super(mensaje);
    }

    public ChoqueConcurrente(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
