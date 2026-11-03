// IA_ · Pruebas de lo que auth.js calcula sin navegador (PKCE y lectura del token) con node:test, sin npm.
// Quién: IA (Claude), 3-nov-2026, a pedido de Jeronimo · Estado: SIN REVISAR
// Manda por encima: RFC 7636 (PKCE) · despliegue/keycloak/realm-florlogic.json (cliente celular)
// Se corre desde codigo/: node --test "backend-finca/src/test/js/*.test.mjs"  (el viaje a Keycloak lo prueba IA_probar-app.mjs)
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { base64url, reclamos, retoS256 } from '../../main/resources/static/app/js/auth.js';

test('el reto S256 es el del ejemplo del RFC 7636 (apéndice B)', async () => {
    assert.equal(await retoS256('dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk'), 'E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM');
});

test('base64url no lleva relleno ni + ni /', () => {
    assert.equal(base64url(new Uint8Array([0xfb, 0xff, 0xbf])), '-_-_');
    assert.equal(base64url(new Uint8Array([1])), 'AQ');
});

test('del token se leen la persona, el jti y los roles, también con acentos', () => {
    const carga = { sub: 's-1', jti: 'j-1', preferred_username: 'operaria.núñez', realm_access: { roles: ['operador'] } };
    const parte = Buffer.from(JSON.stringify(carga)).toString('base64url');
    assert.deepEqual(reclamos(`e30.${parte}.firma`), carga);
    assert.deepEqual(reclamos('no-es-un-token'), {});
});
