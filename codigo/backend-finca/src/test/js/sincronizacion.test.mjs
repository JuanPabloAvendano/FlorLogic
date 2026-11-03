// IA_ · Pruebas de sincronizacion.js (la bandeja hacia el nodo) con dobles del nodo y del repositorio, con node:test.
// Quién: IA (Claude), 3-nov-2026, a pedido de Jeronimo · Estado: SIN REVISAR
// Manda por encima: CT-1 (abrir, particiones, cierre) · contrato A-33 (reenviar los mismos bytes) y A-49 (inmutable)
// Se corre desde codigo/: node --test "backend-finca/src/test/js/*.test.mjs"  (contra el nodo de verdad: IA_probar-app.mjs)
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { sincronizar } from '../../main/resources/static/app/js/sincronizacion.js';

const CELULAR = '0192f0a0-0000-7000-8000-0000000000aa';

/** Un repositorio en memoria con lo que usa la sincronización, y fijarEnvio con la misma regla que el de IndexedDB. */
function repoCon(capturas) {
    const outbox = new Map();
    const lineas = new Map();
    let ultimo = 0;
    for (const { captura, secciones } of capturas) {
        outbox.set(captura.id, { id: captura.id, capturaId: captura.id, estado: 'pendiente', intentos: 0, ultimoError: null });
        lineas.set(captura.id, secciones);
    }
    const porId = new Map(capturas.map((c) => [c.captura.id, c.captura]));
    return {
        ultima: null,
        outbox: async () => [...outbox.values()].map((i) => ({ ...i })),
        item: (id) => outbox.get(id),
        dispositivo: () => CELULAR,
        capturaPorId: async (id) => porId.get(id),
        lineasDe: async (id) => lineas.get(id),
        guardarItemOutbox: async (i) => { outbox.set(i.id, { ...i }); },
        anotar: async () => { },
        async fijarEnvio(id, armar) {
            const item = outbox.get(id);
            if (item.particion)
                return { ...item };
            const { texto, cuantas } = armar(ultimo + 1);
            const fijado = { ...item, particion: texto, dispositivo: CELULAR, consecutivos: [ultimo + 1, ultimo + cuantas] };
            ultimo += cuantas;
            outbox.set(id, fijado);
            return { ...fijado };
        },
        async guardarUltimaSincronizacion(r) { this.ultima = r; },
    };
}

/** Un nodo que anota lo que recibe y responde lo que diga `responder` por cada entrega. */
function nodoQue(responder = () => ({ estado: 200 })) {
    const nodo = {
        abiertas: [], entregas: [], cierres: [],
        abrir: async (token, cuerpo) => {
            nodo.abiertas.push({ token, ...cuerpo });
            return { estado: 201, datos: { sesion_uuid: 's-1', desfase_ms: 250, confirmado_hasta: 0, version_config: 12, marcada: false } };
        },
        entregar: async (token, sesion, particion) => {
            nodo.entregas.push(particion);
            const r = responder(nodo.entregas.length, particion);
            if (r.estado !== 200)
                return { estado: r.estado, datos: r.datos ?? null };
            const p = JSON.parse(particion);
            return {
                estado: 200, datos: {
                    particion_uuid: p.particion_uuid, confirmado_hasta: p.anotaciones.at(-1).consecutivo, conteo: {
                        recibidas: p.anotaciones.length, aplicadas: p.anotaciones.length, marcadas: 0, rechazadas: 0, sin_efecto: 0,
                    }, excepciones: [],
                },
            };
        },
        cerrar: async (token, sesion, completa) => {
            nodo.cierres.push(completa);
            return { estado: 200, datos: { sesion_uuid: sesion, estado: completa ? 'CERRADA' : 'PARCIAL', confirmado_hasta: 4, avisos: [] } };
        },
    };
    return nodo;
}

const credencialCon = (token) => {
    const c = { olvidada: false, token: async () => token, persona: () => ({ sub: 'sub-op', jti: 'j' }), olvidar: () => { c.olvidada = true; } };
    return c;
};

const CATALOGO = {
    cama: (id) => ({ id, codigo: id.split('-').pop(), bloqueId: 'blq-12' }),
    bloque: () => ({ codigo: '12' }),
};
const camaCerrada = (n) => ({
    captura: { id: `0192f0a0-1000-7000-8000-00000000000${n}`, camaId: `blq-12-cma-${n}`, bloqueId: 'blq-12', fecha: '2026-11-03', creadaEn: '2026-11-03T12:00:00Z' },
    secciones: [{ variedadId: 'var-x', lineas: 10, cantidad: 140, obse: '', actualizadaEn: '2026-11-03T12:01:00Z' }],
});

let siguiente = 0;
const uuid = () => `0192f0a0-2000-7000-8000-${String(++siguiente).padStart(12, '0')}`;
const sincronizarCon = (repo, nodo, credencial) => sincronizar({ repo, nodo, credencial, catalogo: CATALOGO, motor: 'reglas.v1', uuid, ahora: () => 1_792_130_531_000 });

test('sin sesión no se abre nada y se pide entrar', async () => {
    const repo = repoCon([camaCerrada(1)]);
    const nodo = nodoQue();
    const r = await sincronizarCon(repo, nodo, credencialCon(null));
    assert.equal(r.necesitaEntrar, true);
    assert.equal(nodo.abiertas.length, 0);
    assert.equal(repo.item(camaCerrada(1).captura.id).particion, undefined); // nada se fijó todavía
});

test('abre una sesión, entrega cada cama en orden con consecutivos seguidos y la cierra completa', async () => {
    const repo = repoCon([camaCerrada(2), camaCerrada(1)]);
    const nodo = nodoQue();
    const r = await sincronizarCon(repo, nodo, credencialCon('tk'));
    assert.deepEqual(nodo.abiertas, [{ token: 'tk', dispositivo: CELULAR, relojMs: 1_792_130_531_000, versionConfig: 12 }]);
    assert.equal(nodo.entregas.length, 2);
    const [a, b] = nodo.entregas.map((t) => JSON.parse(t));
    assert.equal(a.sesiones_captura[0].uuid, camaCerrada(1).captura.id); // UUID v7 menor primero
    assert.deepEqual([...a.anotaciones, ...b.anotaciones].map((x) => x.consecutivo), [1, 2, 3, 4]);
    assert.equal(a.anotaciones[0].tiempo.desfase_declarado_ms, 250); // el que midió el nodo al abrir
    assert.deepEqual(nodo.cierres, [true]);
    assert.equal(r.entregadas, 2);
    assert.equal(r.pendientes, 0);
    assert.equal(repo.item(camaCerrada(1).captura.id).estado, 'enviado');
    assert.equal(repo.ultima.estado, 'CERRADA');
});

test('sin respuesta se para, cierra parcial y el reintento manda los mismos bytes y consecutivos', async () => {
    const repo = repoCon([camaCerrada(1), camaCerrada(2)]);
    const primera = nodoQue(() => ({ estado: 0 }));
    const r1 = await sincronizarCon(repo, primera, credencialCon('tk'));
    assert.equal(primera.entregas.length, 1); // no sigue con la segunda: sin red tampoco entraría
    assert.deepEqual(primera.cierres, [false]);
    assert.equal(r1.pendientes, 2);
    const fijada = repo.item(camaCerrada(1).captura.id);
    assert.deepEqual(fijada.consecutivos, [1, 2]);
    assert.match(fijada.ultimoError, /Sin conexión/);

    const segunda = nodoQue();
    await sincronizarCon(repo, segunda, credencialCon('tk'));
    assert.equal(segunda.entregas[0], primera.entregas[0]); // A-33: los mismos bytes
    assert.deepEqual(JSON.parse(segunda.entregas[1]).anotaciones.map((x) => x.consecutivo), [3, 4]);
    assert.equal(repo.item(camaCerrada(1).captura.id).intentos, 2);
});

test('un 401 al abrir olvida la sesión y pide entrar', async () => {
    const repo = repoCon([camaCerrada(1)]);
    const nodo = nodoQue();
    nodo.abrir = async () => ({ estado: 401, datos: { type: 'https://florlogic.co/problemas/ingesta/credencial-invalida' } });
    const credencial = credencialCon('vencido');
    const r = await sincronizarCon(repo, nodo, credencial);
    assert.equal(r.necesitaEntrar, true);
    assert.equal(credencial.olvidada, true);
    assert.equal(nodo.entregas.length, 0);
});

test('un celular bloqueado no entrega nada y lo dice', async () => {
    const repo = repoCon([camaCerrada(1)]);
    const nodo = nodoQue();
    nodo.abrir = async () => ({ estado: 403, datos: { type: 'https://florlogic.co/problemas/ingesta/sin-permiso', codigo: 'dispositivo_bloqueado' } });
    const r = await sincronizarCon(repo, nodo, credencialCon('tk'));
    assert.match(r.detalle.join(' '), /bloqueó este celular/);
    assert.equal(nodo.entregas.length, 0);
});

test('una cama que no cabe en una partición no gasta consecutivos, lo dice y las demás se entregan', async () => {
    const enorme = camaCerrada(1);
    enorme.secciones = Array.from({ length: 101 }, () => ({ ...camaCerrada(1).secciones[0] })); // 202 datos > 200
    const repo = repoCon([enorme, camaCerrada(2)]);
    const nodo = nodoQue();
    const r = await sincronizarCon(repo, nodo, credencialCon('tk'));
    assert.equal(nodo.entregas.length, 1);
    assert.deepEqual(JSON.parse(nodo.entregas[0]).anotaciones.map((x) => x.consecutivo), [1, 2]);
    assert.match(repo.item(enorme.captura.id).ultimoError, /no caben en una partición/);
    assert.equal(repo.item(enorme.captura.id).particion, undefined);
    assert.equal(r.entregadas, 1);
    assert.deepEqual(nodo.cierres, [false]);
});

test('una partición que el nodo no admite (422) se queda y las demás siguen', async () => {
    const repo = repoCon([camaCerrada(1), camaCerrada(2)]);
    const nodo = nodoQue((n) => (n === 1 ? { estado: 422 } : { estado: 200 }));
    const r = await sincronizarCon(repo, nodo, credencialCon('tk'));
    assert.equal(nodo.entregas.length, 2);
    assert.equal(r.entregadas, 1);
    assert.equal(repo.item(camaCerrada(1).captura.id).estado, 'pendiente');
    assert.equal(repo.item(camaCerrada(2).captura.id).estado, 'enviado');
    assert.deepEqual(nodo.cierres, [false]);
});
