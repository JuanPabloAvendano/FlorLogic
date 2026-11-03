/**
 * Inicio de sesión del celular con Keycloak (CT-2, ID-5): el cliente público `celular` del realm `florlogic`, con
 * código de autorización y PKCE S256, escrito a mano con WebCrypto (sin librería: ninguna dependencia nueva).
 * Keycloak se alcanza por el mismo gateway que sirve la app (`/auth/realms/florlogic`): mismo origen, sin CORS.
 *
 * Capturar no pide sesión (CN-13, captura sin red): la sesión se pide al sincronizar. Los tokens se guardan en
 * localStorage para que la app instalada los recuerde; si Keycloak ya no renueva el de acceso, hay que entrar de
 * nuevo. El refresh token es offline (ALCANCE): dura lo que la sesión offline del realm, no los 30 min del SSO.
 *
 * IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR.
 */
export const CLIENTE = 'celular';
/**
 * offline_access: el refresh token es de una sesión offline (30 días sin uso, offlineSessionIdleTimeout del realm), no
 * de la de SSO (30 min): el operador entra una vez y entrega días después sin volver a escribir la clave (ID-5).
 */
export const ALCANCE = 'openid offline_access';
export const KEYCLOAK = '/auth/realms/florlogic/protocol/openid-connect';
const CLAVE_SESION = 'florlogic.sesion';
/** En sessionStorage: lo que hace falta para terminar el viaje de ida y vuelta a Keycloak en esta pestaña. */
const CLAVE_VIAJE = 'florlogic.viaje';
/** Se renueva un poco antes de que venza: el nodo lo valida al recibir, no al salir del celular. */
const MARGEN_MS = 30_000;

/** Base64 sin relleno y apto para URL (RFC 7636 §3). */
export function base64url(bytes) {
    let binario = '';
    for (const b of bytes)
        binario += String.fromCharCode(b);
    return btoa(binario).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}
/** El reto PKCE: BASE64URL(SHA-256(verificador)) (RFC 7636 §4.2). */
export async function retoS256(verificador) {
    const huella = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(verificador));
    return base64url(new Uint8Array(huella));
}
function azar(bytes) {
    const b = new Uint8Array(bytes);
    crypto.getRandomValues(b);
    return base64url(b);
}
/** Lo que dice el token, sin validarlo: lo valida el nodo. Aquí solo se lee para mostrar y para la partición. */
export function reclamos(jwt) {
    try {
        const parte = jwt.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        const texto = atob(parte + '='.repeat((4 - (parte.length % 4)) % 4));
        return JSON.parse(new TextDecoder().decode(Uint8Array.from(texto, (c) => c.charCodeAt(0))));
    }
    catch {
        return {};
    }
}
/** La única dirección de regreso que acepta el cliente `celular` (`${FLORLOGIC_NODO_URL}/app/*`). */
function regreso() {
    return `${location.origin}/app/`;
}
function leer() {
    try {
        return JSON.parse(localStorage.getItem(CLAVE_SESION) ?? 'null');
    }
    catch {
        return null;
    }
}
function guardar(sesion) {
    localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
}
export function olvidar() {
    try {
        localStorage.removeItem(CLAVE_SESION);
    }
    catch { /* sin almacenamiento: no había nada */ }
}
/** Va a Keycloak a iniciar sesión. `volverA` es la pantalla a la que se regresa (por ejemplo, la bandeja). */
export async function entrar(volverA) {
    const verificador = azar(32);
    const estado = azar(16);
    sessionStorage.setItem(CLAVE_VIAJE, JSON.stringify({ verificador, estado, volverA: volverA ?? null }));
    const url = new URL(`${KEYCLOAK}/auth`, location.origin);
    url.search = new URLSearchParams({
        client_id: CLIENTE,
        response_type: 'code',
        scope: ALCANCE,
        redirect_uri: regreso(),
        state: estado,
        code_challenge: await retoS256(verificador),
        code_challenge_method: 'S256',
    }).toString();
    location.assign(url.toString());
}
/**
 * Si la página es el regreso de Keycloak, canjea el código por los tokens y limpia la dirección. Devuelve la pantalla
 * a la que había que volver, o `undefined` si la página no era un regreso.
 */
export async function alVolver() {
    const p = new URLSearchParams(location.search);
    if (!p.has('state'))
        return undefined;
    let viaje = null;
    try {
        viaje = JSON.parse(sessionStorage.getItem(CLAVE_VIAJE) ?? 'null');
        sessionStorage.removeItem(CLAVE_VIAJE);
    }
    catch { /* sin almacenamiento: el viaje no se puede terminar */ }
    history.replaceState(null, '', regreso());
    if (p.has('error'))
        throw new Error(`Keycloak no dejó entrar: ${p.get('error_description') ?? p.get('error')}.`);
    if (!viaje || viaje.estado !== p.get('state') || !p.get('code'))
        throw new Error('El regreso de Keycloak no corresponde a este inicio de sesión. Entrá de nuevo.');
    await pedirTokens({
        grant_type: 'authorization_code',
        code: p.get('code'),
        redirect_uri: regreso(),
        code_verifier: viaje.verificador,
    });
    return viaje.volverA;
}
/** Pide tokens al endpoint de Keycloak. Si Keycloak ya no los da (código o renovación vencidos), olvida la sesión. */
async function pedirTokens(campos) {
    const r = await fetch(`${KEYCLOAK}/token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ client_id: CLIENTE, ...campos }),
        cache: 'no-store',
    });
    if (r.status === 400 || r.status === 401) {
        olvidar();
        return false;
    }
    if (!r.ok)
        throw new Error(`Keycloak respondió ${r.status} al pedir el token.`);
    const t = await r.json();
    guardar({
        acceso: t.access_token,
        renovacion: t.refresh_token ?? null,
        identidad: t.id_token ?? null,
        vence: Date.now() + (t.expires_in ?? 60) * 1000,
    });
    return true;
}
/**
 * Un token de acceso que todavía sirve, renovándolo si hace falta. `null` si hay que entrar de nuevo. Sin red, la
 * renovación lanza: quien llama lo trata como «sin conexión», no como «sin sesión».
 */
export async function tokenVigente() {
    const s = leer();
    if (!s)
        return null;
    if (s.vence - MARGEN_MS > Date.now())
        return s.acceso;
    if (!s.renovacion) {
        olvidar();
        return null;
    }
    const renovado = await pedirTokens({ grant_type: 'refresh_token', refresh_token: s.renovacion });
    return renovado ? leer()?.acceso ?? null : null;
}
/** Quién está en este celular, según el último token: `null` si nadie ha entrado. */
export function sesion() {
    const s = leer();
    if (!s)
        return null;
    const c = reclamos(s.acceso);
    return {
        usuario: c.preferred_username ?? c.sub ?? 'sin nombre',
        sub: c.sub ?? null,
        jti: c.jti ?? null,
        roles: c.realm_access?.roles ?? [],
    };
}
/** Cierra la sesión aquí y en Keycloak, y vuelve a la app. */
export function salir() {
    const s = leer();
    olvidar();
    // La sesión offline no se cierra con el logout del navegador: se revoca su refresh token. sendBeacon sobrevive a la
    // navegación de la línea siguiente.
    if (s?.renovacion && navigator.sendBeacon) {
        navigator.sendBeacon(`${KEYCLOAK}/revoke`, new URLSearchParams({
            client_id: CLIENTE, token: s.renovacion, token_type_hint: 'refresh_token',
        }));
    }
    const url = new URL(`${KEYCLOAK}/logout`, location.origin);
    url.search = new URLSearchParams({
        client_id: CLIENTE,
        post_logout_redirect_uri: regreso(),
        ...(s?.identidad ? { id_token_hint: s.identidad } : {}),
    }).toString();
    location.assign(url.toString());
}
