// IA_ · La app del celular (PWA, F8) contra un nodo de verdad, sin navegador: lo que haría el celular, con el mismo JS.
// Quién: IA (Claude), 3-nov-2026, a pedido de Jeronimo · Estado: SIN REVISAR
// Manda por encima: codigo/IA_CONSTRUCCION-TX1.md (CT-1, CT-2, CT-4) · despliegue/keycloak/IA_LEEME-identidad.md (ID-5)
// Lo corre scripts/IA_probar-app.sh, que levanta el nodo y crea el operador. Variables:
//   FLORLOGIC_NODO_URL (https://nombre:puerto, la misma del nodo), FLORLOGIC_OPERADOR y FLORLOGIC_OPERADOR_CLAVE,
//   NODE_EXTRA_CA_CERTS (la raíz de la finca: Node la lee al arrancar).
//
// 1. Baja la app de /app/ como el navegador.
// 2. Entra como el navegador: PKCE con el cliente «celular», el formulario de Keycloak por el gateway y su WAF, y el
//    canje del código; el reto se calcula con retoS256 de auth.js.
// 3. Sincroniza dos camas con sincronizacion.js y nodo.js de la app (una con un dato que falta: va marcada).
// 4. Las reenvía tal cual (los mismos bytes): el nodo las cuenta sin efecto.
// 5. Renueva el token con el refresh token offline.
import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import { ALCANCE, CLIENTE, KEYCLOAK, base64url, reclamos, retoS256 } from '../backend-finca/src/main/resources/static/app/js/auth.js';
import { NodoDeFinca } from '../backend-finca/src/main/resources/static/app/js/nodo.js';
import { sincronizar } from '../backend-finca/src/main/resources/static/app/js/sincronizacion.js';
import { uuidv7 } from '../backend-finca/src/main/resources/static/app/js/id.js';

const NODO = process.env.FLORLOGIC_NODO_URL;
const USUARIO = process.env.FLORLOGIC_OPERADOR;
const CLAVE = process.env.FLORLOGIC_OPERADOR_CLAVE;
assert.ok(NODO && USUARIO && CLAVE, 'faltan FLORLOGIC_NODO_URL, FLORLOGIC_OPERADOR o FLORLOGIC_OPERADOR_CLAVE');
const REGRESO = `${NODO}/app/`;
const CELULAR = uuidv7();

// ── 1 · La app ─────────────────────────────────────────────────────────────────────────────────────────────────
const index = await fetch(REGRESO);
assert.equal(index.status, 200, 'GET /app/');
assert.match(await index.text(), /js\/main\.js/);
assert.match(index.headers.get('content-security-policy') ?? '', /connect-src 'self'/);
assert.equal((await fetch(`${NODO}/app/sw.js`)).status, 200, 'GET /app/sw.js');
console.log(`1 · La app se baja de ${REGRESO} con su CSP.`);

// ── 2 · Entrar como el navegador ───────────────────────────────────────────────────────────────────────────────
const galletas = new Map();
async function pedir(url, opciones = {}) {
    const r = await fetch(url, {
        ...opciones,
        redirect: 'manual',
        headers: { ...(opciones.headers ?? {}), Cookie: [...galletas].map(([k, v]) => `${k}=${v}`).join('; ') },
    });
    for (const g of r.headers.getSetCookie()) {
        const [par] = g.split(';');
        const i = par.indexOf('=');
        galletas.set(par.slice(0, i).trim(), par.slice(i + 1));
    }
    return r;
}
const verificador = base64url(crypto.randomBytes(32));
const estado = base64url(crypto.randomBytes(16));
const ida = new URL(`${NODO}${KEYCLOAK}/auth`);
ida.search = new URLSearchParams({
    client_id: CLIENTE, response_type: 'code', scope: ALCANCE, redirect_uri: REGRESO, state: estado,
    code_challenge: await retoS256(verificador), code_challenge_method: 'S256',
}).toString();
const formulario = await pedir(ida);
const html = await formulario.text();
assert.equal(formulario.status, 200, `la página de inicio de Keycloak: ${formulario.status} ${html.slice(0, 300)}`);
const accion = /<form[^>]*id="kc-form-login"[^>]*action="([^"]+)"/.exec(html)?.[1]?.replaceAll('&amp;', '&');
assert.ok(accion, 'el formulario de Keycloak');
const enviado = await pedir(accion, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({ username: USUARIO, password: CLAVE, credentialId: '' }),
});
const vuelta = enviado.headers.get('location') ?? '';
assert.equal(enviado.status, 302, `Keycloak no devolvió a la app (${enviado.status}): ${(await enviado.text()).slice(0, 300)}`);
assert.ok(vuelta.startsWith(REGRESO), `regresa a ${vuelta}`);
const p = new URL(vuelta).searchParams;
assert.equal(p.get('state'), estado);
const canje = await fetch(`${NODO}${KEYCLOAK}/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
        client_id: CLIENTE, grant_type: 'authorization_code', code: p.get('code'), redirect_uri: REGRESO, code_verifier: verificador,
    }),
});
const tokens = await canje.json();
assert.equal(canje.status, 200, `canje del código: ${JSON.stringify(tokens)}`);
const acceso = reclamos(tokens.access_token);
assert.ok([].concat(acceso.aud).includes('backend-finca'), `audiencia ${acceso.aud}`);
assert.ok(acceso.realm_access?.roles?.includes('operador'), 'rol operador');
assert.equal(reclamos(tokens.refresh_token).typ, 'Offline', 'el refresh token es offline (ID-5)');
console.log(`2 · ${acceso.preferred_username} entró con PKCE por el gateway; token para backend-finca con rol operador y refresh offline.`);

// ── 3 · Sincronizar dos camas ──────────────────────────────────────────────────────────────────────────────────
const hoy = new Date().toISOString().slice(0, 10);
const camas = [37, 39].map((n) => ({
    captura: { id: uuidv7(), camaId: `blq-12-cma-${n}`, bloqueId: 'blq-12', fecha: hoy, creadaEn: new Date().toISOString() },
    secciones: [
        { variedadId: 'var-scarlett', lineas: 126, cantidad: 1860, obse: '', actualizadaEn: new Date().toISOString() },
        { variedadId: 'var-pink', lineas: n === 39 ? null : 231, cantidad: 3225, obse: 'prueba', actualizadaEn: new Date().toISOString() },
    ],
}));
const repo = repoEnMemoria(camas);
const credencial = { token: async () => tokens.access_token, persona: () => ({ sub: acceso.sub, jti: acceso.jti }), olvidar: () => { } };
const catalogo = { cama: (id) => ({ codigo: id.split('-').pop(), bloqueId: 'blq-12' }), bloque: () => ({ codigo: '12' }) };
const nodo = new NodoDeFinca(undefined, NODO);

const primera = await sincronizar({ repo, nodo, credencial, catalogo, motor: 'reglas.v1' });
console.log(`3 · ${primera.detalle.join(' ')}`);
assert.equal(primera.entregadas, 2, JSON.stringify(primera));
for (const item of await repo.outbox()) {
    console.log(`    consecutivos ${item.consecutivos.join('–')}: ${JSON.stringify(item.resultado.conteo)} ${item.resultado.excepciones.join(' | ')}`);
}
// Por cama: 2 secciones × (lineas, cantidad) + la obse escrita = 5 anotaciones; la cama 39 trae una sin valor.
assert.equal(primera.cierre.confirmadoHasta, 10, 'el nodo confirma los 10 consecutivos, sin huecos');
const conteos = (await repo.outbox()).map((i) => i.resultado.conteo);
assert.deepEqual(conteos.map((c) => c.aplicadas), [5, 5]);
assert.ok(conteos.every((c) => c.rechazadas === 0), 'nada rechazado');
assert.ok(conteos.every((c) => c.marcadas >= 1), 'el celular no aprobado entra marcado (CT-10)');

// ── 4 · Reenviar los mismos bytes ──────────────────────────────────────────────────────────────────────────────
for (const item of await repo.outbox()) {
    await repo.guardarItemOutbox({ ...item, estado: 'pendiente', resultado: undefined });
}
const segunda = await sincronizar({ repo, nodo, credencial, catalogo, motor: 'reglas.v1' });
const sinEfecto = (await repo.outbox()).map((i) => i.resultado.conteo.sin_efecto);
console.log(`4 · Reenvío: ${segunda.detalle[0]} Sin efecto: ${sinEfecto.join(' y ')}.`);
assert.deepEqual(sinEfecto, (await repo.outbox()).map((i) => i.consecutivos[1] - i.consecutivos[0] + 1));

// ── 5 · Renovar con el refresh token offline ───────────────────────────────────────────────────────────────────
const renovado = await fetch(`${NODO}${KEYCLOAK}/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({ client_id: CLIENTE, grant_type: 'refresh_token', refresh_token: tokens.refresh_token }),
});
assert.equal(renovado.status, 200, 'renovar con el refresh token offline');
console.log(`5 · El refresh token offline renueva el de acceso.`);
console.log(`CELULAR=${CELULAR}`);

function repoEnMemoria(capturas) {
    const outbox = new Map();
    const lineas = new Map(capturas.map((c) => [c.captura.id, c.secciones]));
    const porId = new Map(capturas.map((c) => [c.captura.id, c.captura]));
    let ultimo = 0;
    for (const { captura } of capturas) {
        outbox.set(captura.id, { id: captura.id, capturaId: captura.id, estado: 'pendiente', intentos: 0, ultimoError: null });
    }
    return {
        outbox: async () => [...outbox.values()].sort((a, b) => a.id.localeCompare(b.id)).map((i) => ({ ...i })),
        dispositivo: () => CELULAR,
        capturaPorId: async (id) => porId.get(id),
        lineasDe: async (id) => lineas.get(id),
        guardarItemOutbox: async (i) => { outbox.set(i.id, { ...i }); },
        anotar: async () => { },
        guardarUltimaSincronizacion: async () => { },
        async fijarEnvio(id, armar) {
            const item = outbox.get(id);
            if (item.particion)
                return { ...item };
            const { texto, cuantas } = armar(ultimo + 1);
            const fijado = { ...item, particion: texto, consecutivos: [ultimo + 1, ultimo + cuantas] };
            ultimo += cuantas;
            outbox.set(id, fijado);
            return { ...fijado };
        },
    };
}
