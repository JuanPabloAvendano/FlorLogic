package co.florlogic.finca.ingesta.dominio.recepcion;

/**
 * Lo que devuelve el caso de uso de recibir una partición, con nombre en vez de excepciones (A-57, Special Case):
 * la recepción, o por qué no se recibió nada.
 */
public sealed interface ResultadoDeRecepcion {

    record Recibida(RecepcionDeParticionValor recepcion) implements ResultadoDeRecepcion {
    }

    /** No hay sesión abierta con ese id (contrato A-22: {@code 404 sesion-desconocida}). */
    record SesionDesconocida() implements ResultadoDeRecepcion {
    }

    /** La sesión la abrió otra persona (contrato A-51: {@code 403 sesion_ajena}). */
    record SesionAjena() implements ResultadoDeRecepcion {
    }
}
