package co.florlogic.contrato.codigos;

import java.util.Optional;

import co.florlogic.contrato.lectura.IdDeCatalogoInterfaz;

/**
 * Los códigos que el ingreso pone en {@code excepciones[].codigos} (A-23, ESPEC §5). Aquí solo
 * está el nombre; <em>cuándo</em> se pone cada uno es lógica del ingreso y vive en su semilla.
 * Los ids de regla del catálogo ({@code R-…}) no están aquí: los crea la configuración.
 */
public enum CodigoIngreso implements IdDeCatalogoInterfaz {

    // Forma y versión
    /** Fallo técnico: forma inválida, campo desconocido o tope pasado (A-32). */
    DATOS_INVALIDOS("datos_invalidos"),
    /** Una versión de configuración, app o motor que el nodo de finca no creó; es anomalía (A-50). */
    VERSION_DESCONOCIDA("version_desconocida"),

    // Dato ausente y reglas
    /** Falta el valor, con o sin motivo (A-35). */
    INCOMPLETA("incompleta"),
    /** Falta el valor y no trae motivo (A-35). */
    MOTIVO_AUSENTE("motivo_ausente"),
    /** Una regla detectó una desviación brusca y no vino motivo (A-49 ⟦4⟧). */
    DESVIACION_SIN_MOTIVO("desviacion_sin_motivo"),
    /** Los motores del celular y del nodo de finca juzgaron distinto (A-32). */
    DIVERGENCIA("divergencia"),
    /** Llegó sin el mínimo requerido de su actividad (A-46). */
    SIN_MINIMO("sin_minimo"),

    // Tiempo
    HORA_DUDOSA("hora_dudosa"),
    /** El celular nunca sincronizó y no declaró desfase (A-16). */
    DESFASE_DESCONOCIDO("desfase_desconocido"),
    /** El desfase declarado al capturar y el medido al entregar difieren más que el umbral. */
    RELOJ_MOVIDO("reloj_movido"),
    /** Capturada después de vencer la credencial de su sesión de captura (ES-S12). */
    CAPTURADA_SIN_CREDENCIAL("capturada_sin_credencial"),
    /** Su edad hasta la primera llegada pasa el umbral (A-45 ⟦2⟧). */
    VIEJA("vieja"),

    // Consecutivos y dispositivo
    /** Llegó después de un hueco en los consecutivos (A-27). */
    HUECO_CONSECUTIVO("hueco_consecutivo"),
    /** El mismo consecutivo del mismo dispositivo con otro id (A-39). */
    CONSECUTIVO_REPETIDO("consecutivo_repetido"),
    /** El dispositivo, ya registrado de nuevo, la reenvió (A-39). */
    CONSECUTIVO_RESUELTO("consecutivo_resuelto"),
    DISPOSITIVO_NO_REGISTRADO("dispositivo_no_registrado"),

    // Órdenes de captura
    /** Llegó tarde para una orden ya cumplida; cuenta {@code sin_efecto} (A-45 ⟦1⟧). */
    OBSOLETA("obsoleta"),
    ORDEN_DUPLICADA("orden_duplicada"),
    FUERA_DE_ASIGNACION("fuera_de_asignacion");

    private final String id;

    CodigoIngreso(String id) {
        this.id = id;
    }

    /** El id en {@code snake_case} tal como viaja en el JSON (§3b). */
    @Override
    public String id() {
        return id;
    }

    /**
     * Busca un código que llegó en {@code codigos[]}. Vacío si es un id de regla del catálogo
     * ({@code R-…}) o un código que este lado todavía no conoce: el texto se conserva en la respuesta
     * y se muestra tal cual, nunca se pierde ni se lanza (A-57).
     */
    public static Optional<CodigoIngreso> de(String id) {
        return Optional.ofNullable(IdDeCatalogoInterfaz.buscar(values(), id, null));
    }
}
