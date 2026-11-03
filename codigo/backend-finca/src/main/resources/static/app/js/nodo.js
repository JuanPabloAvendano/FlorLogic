/**
 * El nodo de la finca visto desde el celular (CT-1, CT-8): abrir la sesión de sincronización, entregar particiones en
 * ella y cerrarla. Es la misma conversación que tiene el simulador (`NodoDeFincaAdaptadorHttp`), por el mismo gateway
 * que sirve la app: mismo origen, sin CORS. Nunca lanza: devuelve el estado HTTP (0 si no hubo respuesta) y el JSON.
 *
 * IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR.
 */
export const CONTRATO = '1.0';
const SESIONES = '/ingesta/v1/sesiones';
/** El celular espera 30 s y reintenta: la partición es idempotente (contrato §3, A-58 del gateway). */
const ESPERA_MS = 30_000;

export class NodoDeFinca {
    pedir;
    origen;
    /** `pedir` y `origen` se cambian en las pruebas; en la app son `fetch` y el mismo origen. */
    constructor(pedir = (url, opciones) => fetch(url, opciones), origen = '') {
        this.pedir = pedir;
        this.origen = origen;
    }
    abrir(token, { dispositivo, relojMs, versionConfig }) {
        return this.post(token, SESIONES, JSON.stringify({
            contrato: CONTRATO,
            dispositivo_uuid: dispositivo,
            reloj_ms: relojMs,
            version_config: versionConfig,
        }));
    }
    /** Manda los bytes de la partición tal como se armaron: un reintento manda los mismos (A-33). */
    entregar(token, sesion, particion) {
        return this.post(token, `${SESIONES}/${encodeURIComponent(sesion)}/particiones`, particion);
    }
    cerrar(token, sesion, completa) {
        return this.post(token, `${SESIONES}/${encodeURIComponent(sesion)}/cierre`, JSON.stringify({ contrato: CONTRATO, completa }));
    }
    async post(token, ruta, cuerpo) {
        let r;
        try {
            r = await this.pedir(this.origen + ruta, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
                body: cuerpo,
                cache: 'no-store',
                signal: AbortSignal.timeout(ESPERA_MS),
            });
        }
        catch {
            return { estado: 0, datos: null };
        }
        let datos = null;
        try {
            datos = await r.json();
        }
        catch { /* sin cuerpo JSON: basta el estado */ }
        return { estado: r.status, datos };
    }
}
/**
 * Lo que una respuesta que no es la esperada le dice a la persona: qué pasó y qué hacer. Los `type` son los de los
 * problemas del nodo (contrato A-22) y del gateway (A-3).
 */
export function describir({ estado, datos }) {
    const tipo = String(datos?.type ?? '').split('/').pop();
    if (estado === 0)
        return 'Sin conexión con el nodo. Queda en la bandeja y se reintenta.';
    if (estado === 401)
        return 'La sesión venció o no es válida. Entrá de nuevo.';
    if (estado === 403 && tipo === 'bloqueado')
        return 'El WAF del nodo la frenó. Queda en la bandeja; el administrador lo revisa con la traza ' + (datos?.traceId ?? '') + '.';
    if (estado === 403 && /bloquead/.test(JSON.stringify(datos ?? {})))
        return 'El administrador bloqueó este celular. No se entrega nada hasta que lo apruebe.';
    if (estado === 403)
        return 'Tu usuario no tiene permiso para entregar (rol operador).';
    if (estado === 404 && tipo === 'sesion-desconocida')
        return 'El nodo ya no reconoce la sesión de sincronización. Se reintenta en la próxima.';
    if (estado === 404)
        return 'Esa dirección no es la del nodo (404). Abrí la app desde el nodo de la finca.';
    if (estado === 413)
        return 'La partición es demasiado grande para el nodo.';
    if (estado === 422)
        return 'El nodo no la admite (422): es un error de la app, no de lo capturado. Avisá al administrador.';
    if (estado >= 500)
        return `El nodo no está disponible (${estado}). Queda en la bandeja y se reintenta.`;
    return `El nodo respondió ${estado}${tipo ? ` (${tipo})` : ''}.`;
}
