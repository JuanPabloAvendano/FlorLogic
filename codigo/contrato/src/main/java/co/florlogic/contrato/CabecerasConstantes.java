package co.florlogic.contrato;

/**
 * Nombres y valores de cabecera que comparten el celular y el nodo de finca (A-13, A-29, A-36).
 * Son texto plano para no atar el contrato a Spring ni a ningún cliente HTTP.
 */
public final class CabecerasConstantes {

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

    private CabecerasConstantes() {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - public final class + constructor private: nadie hereda ni crea objetos de esta clase; solo se usan sus
 *   constantes (CabecerasConstantes.RETRY_AFTER). Es la forma Java de un «archivo de constantes».
 * - static final String: constante de clase; el compilador copia el texto donde se use.
 * - "Authorization" / "Bearer ": cabecera HTTP estándar con el esquema Bearer (RFC 6750): quien la lleva presenta
 *   un token y el servidor no pregunta más. Aquí el token es el JWS que el nodo valida.
 * - "traceparent": cabecera de W3C Trace Context; lleva el id de la traza para seguir una petición entre procesos.
 * - "Cache-Control: no-store": prohíbe a cualquier caché (proxy, gateway, navegador) guardar la respuesta.
 * - "Retry-After": dice al cliente cuántos segundos esperar antes de reintentar.
 * - "application/problem+json": el tipo de los errores con forma «Problem Details» (RFC 9457): un JSON con
 *   type, title, status y detail.
 */
