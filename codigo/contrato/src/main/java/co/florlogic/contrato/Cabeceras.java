package co.florlogic.contrato;

/**
 * Nombres y valores de cabecera que comparten el celular y el nodo de finca (A-13, A-29, A-36).
 * Son texto plano para no atar el contrato a Spring ni a ningún cliente HTTP.
 */
public final class Cabeceras {

    /** Lleva la credencial de quien entrega: {@code Authorization: Bearer <JWS>} (A-29, A-51). */
    public static final String AUTHORIZATION = "Authorization";

    /** Prefijo del valor de {@link #AUTHORIZATION}, con su espacio. El JWS va después. */
    public static final String ESQUEMA_BEARER = "Bearer ";

    /** Traza W3C opcional; el nodo de finca la propaga a sus logs (A-29). */
    public static final String TRACEPARENT = "traceparent";

    public static final String CACHE_CONTROL = "Cache-Control";

    /** Ninguna respuesta de ingesta se guarda en caché: un reenvío se procesa de nuevo (A-13, A-26). */
    public static final String NO_STORE = "no-store";

    /** Lo traen {@code 429}, {@code 503} y {@code 409 particion-en-curso} (A-36). */
    public static final String RETRY_AFTER = "Retry-After";

    public static final String TIPO_JSON = "application/json";

    /** Los errores de toda la partición (A-22). */
    public static final String TIPO_PROBLEMA = "application/problem+json";

    private Cabeceras() {
    }
}
