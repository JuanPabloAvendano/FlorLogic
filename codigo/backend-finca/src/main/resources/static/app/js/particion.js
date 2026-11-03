/**
 * De una cama cerrada a una partición del contrato de ingesta (ESPEC-contrato-ingesta): la única traducción entre el
 * modelo de la demo (captura → secciones con variedad, #líneas y cantidad) y lo que el nodo ya recibe hoy, anotaciones
 * `captura.campo` versión 1. Sin efectos: no toca la base, ni la red, ni el reloj (todo entra por parámetro), así que
 * se prueba con Node (`src/test/js`).
 *
 * Cómo se traduce (decisión por defecto de la sesión del 3-nov-2026, por revisar con Juan):
 *  - una anotación por sección × campo: `lineas` y `cantidad` siempre, `obse` solo si se escribió;
 *  - `seccion_id` = `B<bloque>-C<cama>-S<n>`, con n la sección en el orden en que se capturó (el formato que ya usa
 *    `/web/asignacion`);
 *  - `produccion_id` = la variedad de la sección; `jornada` = la fecha de la captura;
 *  - `veredicto_local` = `aceptada`, o `marcada` si falta el valor: el nodo marca `incompleta` lo que no trae valor, y
 *    si el celular dijera otra cosa saldría `divergencia` (ReglasDeIngreso);
 *  - una sesión de captura por cama (su uuid es el de la captura), de la persona que entrega.
 *
 * La demo no guarda reloj monótono por línea: `mono_ms` y `arranque` van en 0 (el contrato los admite ≥ 0 y el ingreso
 * todavía no los usa). `crudo_ms` es la última vez que se tocó la sección.
 *
 * IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR.
 */
import { CONTRATO } from './nodo.js';
export const TIPO = 'captura.campo';
export const VERSION_TIPO = 1;
/** La versión de configuración con que captura la app: la vigente del nodo (V5) y la del simulador. */
export const VERSION_CONFIG = 12;
/** La versión de la app que va en la sesión de captura. */
export const VERSION_APP = 'app-captura-0.1-nodo';
/** El techo de anotaciones por partición que el WAF deja pasar entero (WAF A-5): una cama nunca llega. */
export const MAX_ANOTACIONES = 200;

const CAMPOS = [
    { id: 'lineas', valor: (l) => l.lineas, opcional: false },
    { id: 'cantidad', valor: (l) => l.cantidad, opcional: false },
    { id: 'obse', valor: (l) => (l.obse ?? '').trim() || null, opcional: true },
];

export function seccionId(bloque, cama, n) {
    return `B${bloque.codigo}-C${cama.codigo}-S${n}`;
}

/**
 * Arma la partición de una captura cerrada.
 *
 * @param p.captura   la captura (su id es el de la sesión de captura)
 * @param p.lineas    sus secciones, en orden de captura
 * @param p.bloque    el bloque de la cama, con su código
 * @param p.cama      la cama, con su código
 * @param p.persona   quién entrega: `{ sub, jti }` del token
 * @param p.motor     versión del motor de reglas local
 * @param p.desde     el primer consecutivo libre del celular
 * @param p.desfase   el desfase que midió el nodo al abrir la sesión, o null si no se conoce
 * @param p.uuid      generador de UUID v7
 * @returns `{ texto, cuantas }`: el JSON tal como viaja (y se reenvía) y cuántos consecutivos usó
 */
export function armarParticion({ captura, lineas, bloque, cama, persona, motor, desde, desfase, uuid }) {
    const anotaciones = [];
    let consecutivo = desde;
    lineas.forEach((linea, i) => {
        for (const campo of CAMPOS) {
            const valor = campo.valor(linea);
            if (campo.opcional && valor === null)
                continue;
            const datos = {
                produccion_id: linea.variedadId,
                seccion_id: seccionId(bloque, cama, i + 1),
                campo_id: campo.id,
                jornada: captura.fecha,
            };
            // Valor ausente es el dato ausente (A-33): no se escribe null.
            if (valor !== null && valor !== undefined)
                datos.valor = valor;
            const tiempo = { crudo_ms: Date.parse(linea.actualizadaEn), mono_ms: 0, arranque: 0 };
            if (desfase !== null && desfase !== undefined)
                tiempo.desfase_declarado_ms = desfase;
            anotaciones.push({
                anotacion_uuid: uuid(),
                consecutivo: consecutivo++,
                tipo: TIPO,
                version_tipo: VERSION_TIPO,
                sesion_captura_uuid: captura.id,
                tiempo,
                version_config: VERSION_CONFIG,
                veredicto_local: datos.valor === undefined ? 'marcada' : 'aceptada',
                datos,
            });
        }
    });
    if (anotaciones.length > MAX_ANOTACIONES)
        throw new Error(`La cama tiene ${anotaciones.length} datos: más de ${MAX_ANOTACIONES} no caben en una partición.`);
    const particion = {
        contrato: CONTRATO,
        particion_uuid: uuid(),
        sesiones_captura: [{
                uuid: captura.id,
                persona_id: persona.sub,
                credencial_jti: persona.jti ?? 'sin-jti',
                inicio: captura.creadaEn,
                version_app: VERSION_APP,
                version_motor: motor,
            }],
        anotaciones,
    };
    return { texto: JSON.stringify(particion), cuantas: anotaciones.length };
}
