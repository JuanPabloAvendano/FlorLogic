package co.florlogic.contrato.lectura;

/**
 * Un valor de un catálogo cerrado que viaja como texto ({@code "aceptada"}, {@code "sin_efecto"}…).
 *
 * <p>Patrón <em>lector tolerante</em> con enums extensibles (Fowler, TolerantReader; plantilla
 * endpoint EP-16): leer un valor que este lado no conoce nunca lanza; devuelve la variante
 * {@code DESCONOCIDO} del enum, y quien lo usa decide. Del lado del nodo de finca, el validador
 * rechaza esa entrada (A-32, A-50: nada desconocido entra); del lado del celular, una respuesta más
 * nueva se sigue leyendo (A-57).
 */
public interface IdDeCatalogoInterfaz {

    /** El texto tal como viaja en el JSON, o {@code null} para la variante desconocida. */
    String id();

    /** Busca por id; si no existe o es {@code null}, devuelve {@code siNoExiste}. Nunca lanza. */
    static <E extends Enum<E> & IdDeCatalogoInterfaz> E buscar(E[] valores, String id, E siNoExiste) {
        if (id == null) {
            return siNoExiste;
        }
        for (E valor : valores) {
            if (id.equals(valor.id())) {
                return valor;
            }
        }
        return siNoExiste;
    }
}
