Lpackage co.florlogic.finca.ingesta.servicios.puertos.entrada;

/**
 * Puerto de entrada: recibir una partición que mandó un celular (plantilla endpoint T4: un puerto por
 * caso de uso, nombrado por su intención).
 */
public interface RecibirParticionPuertoEntrada {

    /** Guarda cada anotación una sola vez y dice cuántas eran nuevas y cuántas ya estaban. */
    ParticionRecibidaResultado ejecutar(RecibirParticionComando comando);
}
