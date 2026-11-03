// IA_ · Pruebas de particion.js (la cama → la partición del contrato) con node:test, sin npm.
// Quién: IA (Claude), 3-nov-2026, a pedido de Jeronimo · Estado: SIN REVISAR
// Manda por encima: codigo/contrato/espec/ESPEC-contrato-ingesta.md · la partición dorada del contrato
// Se corre desde codigo/: node --test "backend-finca/src/test/js/*.test.mjs"  (Node 22 o más nuevo; ./mvnw verify no las corre)
import { test } from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import { armarParticion, seccionId, VERSION_CONFIG } from '../../main/resources/static/app/js/particion.js';

const DORADA = JSON.parse(fs.readFileSync(new URL('../../../../contrato/src/test/resources/dorados/peticion-particion.json',
    import.meta.url), 'utf8'));

const CAPTURA = {
    id: '0192f0a0-1000-7000-8000-00000000c001', camaId: 'blq-12-cma-37', bloqueId: 'blq-12', fecha: '2026-11-03',
    creadaEn: '2026-11-03T12:00:00.000Z',
};
const LINEAS = [
    { variedadId: 'var-scarlett', lineas: 126, cantidad: 1860, obse: '', actualizadaEn: '2026-11-03T12:01:00.000Z' },
    { variedadId: 'var-pink', lineas: null, cantidad: 3225, obse: ' 1/2 ', actualizadaEn: '2026-11-03T12:02:00.000Z' },
];

function armar(desde = 41, desfase = -1200) {
    let n = 0;
    const uuid = () => `0192f0a0-2000-7000-8000-${String(++n).padStart(12, '0')}`;
    return armarParticion({
        captura: CAPTURA, lineas: LINEAS, bloque: { codigo: '12' }, cama: { codigo: '37' },
        persona: { sub: 'sub-op-1', jti: 'jti-1' }, motor: 'reglas.v1', desde, desfase, uuid,
    });
}

test('una anotación por sección y campo, con consecutivos seguidos desde el primero libre', () => {
    const { texto, cuantas } = armar(41);
    const p = JSON.parse(texto);
    assert.equal(cuantas, 5); // 2 × (lineas, cantidad) + la obse que sí se escribió
    assert.deepEqual(p.anotaciones.map((a) => a.consecutivo), [41, 42, 43, 44, 45]);
    assert.deepEqual(p.anotaciones.map((a) => `${a.datos.seccion_id}/${a.datos.campo_id}`),
        ['B12-C37-S1/lineas', 'B12-C37-S1/cantidad', 'B12-C37-S2/lineas', 'B12-C37-S2/cantidad', 'B12-C37-S2/obse']);
    assert.deepEqual(p.anotaciones.map((a) => a.datos.produccion_id),
        ['var-scarlett', 'var-scarlett', 'var-pink', 'var-pink', 'var-pink']);
    assert.ok(p.anotaciones.every((a) => a.datos.jornada === '2026-11-03' && a.version_config === VERSION_CONFIG));
    assert.equal(p.anotaciones[4].datos.valor, '1/2');
});

test('el dato que falta no viaja como null y la anotación va marcada, como la marcaría el nodo', () => {
    const p = JSON.parse(armar().texto);
    const sinLineas = p.anotaciones[2];
    assert.equal('valor' in sinLineas.datos, false);
    assert.equal(sinLineas.veredicto_local, 'marcada');
    assert.ok(p.anotaciones.filter((a) => a !== sinLineas).every((a) => a.veredicto_local === 'aceptada'));
    assert.equal(armar().texto.includes('null'), false); // A-33: null y ausente valen lo mismo; aquí no hay null
});

test('la forma es la de la partición dorada del contrato: mismos campos en cada nivel', () => {
    const p = JSON.parse(armar().texto);
    const claves = (o) => Object.keys(o).sort();
    assert.deepEqual(claves(p), claves(DORADA));
    assert.deepEqual(claves(p.sesiones_captura[0]), claves(DORADA.sesiones_captura[0]));
    assert.deepEqual(claves(p.anotaciones[0]), claves(DORADA.anotaciones[0]));
    assert.deepEqual(claves(p.anotaciones[0].tiempo), claves(DORADA.anotaciones[0].tiempo));
    assert.deepEqual(claves(p.anotaciones[0].datos), claves(DORADA.anotaciones[0].datos));
    assert.equal(p.contrato, DORADA.contrato);
    assert.equal(p.anotaciones[0].tipo, DORADA.anotaciones[0].tipo);
    assert.equal(p.anotaciones[0].version_tipo, DORADA.anotaciones[0].version_tipo);
});

test('la sesión de captura es la cama, de la persona que entrega, y cada anotación la nombra', () => {
    const p = JSON.parse(armar().texto);
    assert.deepEqual(p.sesiones_captura, [{
        uuid: CAPTURA.id, persona_id: 'sub-op-1', credencial_jti: 'jti-1', inicio: CAPTURA.creadaEn,
        version_app: 'app-captura-0.1-nodo', version_motor: 'reglas.v1',
    }]);
    assert.ok(p.anotaciones.every((a) => a.sesion_captura_uuid === CAPTURA.id));
    assert.notEqual(p.particion_uuid, p.anotaciones[0].anotacion_uuid);
    assert.equal(new Set(p.anotaciones.map((a) => a.anotacion_uuid)).size, 5);
});

test('el desfase va cuando el nodo lo midió y falta cuando no (desconocido, A-16)', () => {
    assert.equal(JSON.parse(armar(1, -1200).texto).anotaciones[0].tiempo.desfase_declarado_ms, -1200);
    assert.equal('desfase_declarado_ms' in JSON.parse(armar(1, null).texto).anotaciones[0].tiempo, false);
    assert.equal(JSON.parse(armar().texto).anotaciones[0].tiempo.crudo_ms, Date.parse(LINEAS[0].actualizadaEn));
});

test('la sección se nombra como en la asignación del administrador', () => {
    assert.equal(seccionId({ codigo: '3' }, { codigo: '17' }, 1), 'B3-C17-S1');
});
