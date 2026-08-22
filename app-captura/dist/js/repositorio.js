/**
 * Repositorio de la demo: catálogo + capturas, todo en el dispositivo.
 *
 * Reglas que ya se ven aquí y que vienen de decisiones cerradas:
 *  - CN-26 · El catálogo se descarga y se versiona ANTES de capturar. Resembrar el catálogo
 *    nunca toca lo capturado: son almacenes distintos y se vacían por separado.
 *  - CNF   · Cada dato queda guardado de inmediato. No hay botón de guardar.
 *  - CNF   · Se puede dejar una cama incompleta y marcarla pendiente  ->  estado 'borrador'.
 *  - CN-24 · Al cerrar, la captura entra a la bandeja de salida con su propio id idempotente.
 *
 * Después de la primera carga, la aplicación lee de aquí y no de `seed.json`: el archivo es el
 * instalador del catálogo, no la fuente en tiempo de ejecución.
 */
import { Almacen } from './almacen.js';
import { idDispositivo, uuidv7 } from './id.js';
export const ESQUEMA = {
    nombre: 'florlogic-captura',
    version: 2,
    almacenes: [
        { nombre: 'meta', clave: 'clave' },
        { nombre: 'bloques', clave: 'id' },
        { nombre: 'camas', clave: 'id', indices: [{ nombre: 'porBloque', campo: 'bloqueId' }] },
        { nombre: 'variedades', clave: 'id' },
        { nombre: 'plantillas', clave: 'id' },
        { nombre: 'siembras', clave: 'id', indices: [{ nombre: 'porCama', campo: 'camaId' }] },
        {
            nombre: 'capturas',
            clave: 'id',
            indices: [
                { nombre: 'porCama', campo: 'camaId' },
                { nombre: 'porEstado', campo: 'estado' },
            ],
        },
        { nombre: 'lineas', clave: 'id', indices: [{ nombre: 'porCaptura', campo: 'capturaId' }] },
        { nombre: 'outbox', clave: 'id', indices: [{ nombre: 'porEstado', campo: 'estado' }] },
        // D8 · instrumentación: de aquí salen las medidas de respuesta de los escenarios.
        { nombre: 'eventos', clave: 'id', indices: [{ nombre: 'porTipo', campo: 'tipo' }] },
        // D6 · el «servidor» de la demo vive en el mismo dispositivo, pero del otro lado del sync.
        { nombre: 'servidor', clave: 'id', indices: [{ nombre: 'porCamaFecha', campo: ['camaId', 'fecha'] }] },
        { nombre: 'conflictos', clave: 'id', indices: [{ nombre: 'porCama', campo: 'camaId' }] },
    ],
};
/** Almacenes del catálogo: se reemplazan al resembrar. Los de captura NUNCA se tocan. */
const ALMACENES_CATALOGO = ['bloques', 'camas', 'variedades', 'plantillas', 'siembras'];
const CLAVE_VERSION = 'versionCatalogo';
const CLAVE_DIAGNOSTICO = 'diagnostico';
const ahora = () => new Date().toISOString();
export class Repositorio {
    almacen;
    versionCatalogo;
    constructor(almacen, versionCatalogo) {
        this.almacen = almacen;
        this.versionCatalogo = versionCatalogo;
    }
    static async abrir() {
        const almacen = await Almacen.abrir(ESQUEMA);
        const meta = await almacen.obtener('meta', CLAVE_VERSION);
        return new Repositorio(almacen, meta?.valor ?? '');
    }
    version() {
        return this.versionCatalogo;
    }
    // -- catálogo ------------------------------------------------------------------
    /**
     * Deja el catálogo local igual al de la semilla. Idempotente: si la versión coincide no escribe.
     * Devuelve true cuando hubo que sembrar.
     */
    async sembrarCatalogo(semilla) {
        const version = `${semilla.version}·${semilla.generado}`;
        if (this.versionCatalogo === version)
            return false;
        await this.almacen.vaciar(ALMACENES_CATALOGO);
        await this.almacen.ponerVarios('bloques', semilla.bloques);
        await this.almacen.ponerVarios('camas', semilla.camas);
        await this.almacen.ponerVarios('variedades', semilla.variedades);
        await this.almacen.ponerVarios('plantillas', semilla.plantillas);
        await this.almacen.ponerVarios('siembras', semilla.siembras);
        const diagnostico = {
            version: semilla.version,
            generado: semilla.generado,
            origen: semilla.origen,
            advertencia: semilla.advertencia,
            empresa: semilla.empresa,
            fincas: semilla.fincas,
            anomalias: semilla.anomalias,
            avisos: semilla.avisos,
        };
        await this.almacen.poner('meta', { clave: CLAVE_DIAGNOSTICO, valor: JSON.stringify(diagnostico) });
        await this.almacen.poner('meta', { clave: CLAVE_VERSION, valor: version });
        this.versionCatalogo = version;
        return true;
    }
    /** Reconstruye la semilla desde la base local. Es lo que la aplicación usa para pintar. */
    async semillaLocal() {
        const meta = await this.almacen.obtener('meta', CLAVE_DIAGNOSTICO);
        if (!meta)
            throw new Error('No hay catálogo sembrado en este dispositivo');
        const diagnostico = JSON.parse(meta.valor);
        const [bloques, camas, variedades, plantillas, siembras] = await Promise.all([
            this.almacen.todos('bloques'),
            this.almacen.todos('camas'),
            this.almacen.todos('variedades'),
            this.almacen.todos('plantillas'),
            this.almacen.todos('siembras'),
        ]);
        return { ...diagnostico, bloques, camas, variedades, plantillas, siembras };
    }
    bloques = () => this.almacen.todos('bloques');
    camas = (bloqueId) => this.almacen.porIndice('camas', 'porBloque', bloqueId);
    variedades = () => this.almacen.todos('variedades');
    plantillas = () => this.almacen.todos('plantillas');
    siembras = (camaId) => this.almacen.porIndice('siembras', 'porCama', camaId);
    // -- captura -------------------------------------------------------------------
    async crearCaptura(datos) {
        const t = ahora();
        const captura = {
            id: uuidv7(),
            camaId: datos.camaId,
            bloqueId: datos.bloqueId,
            plantillaId: datos.plantillaId,
            fecha: datos.fecha,
            estado: 'borrador',
            creadaEn: t,
            actualizadaEn: t,
            cerradaEn: null,
            dispositivoId: idDispositivo(),
            usuario: datos.usuario,
            versionCatalogo: this.versionCatalogo,
        };
        await this.almacen.poner('capturas', captura);
        return captura;
    }
    async agregarLinea(capturaId, datos) {
        const t = ahora();
        const linea = {
            id: uuidv7(),
            capturaId,
            variedadId: datos.variedadId,
            lineas: datos.lineas,
            cantidad: datos.cantidad,
            obse: datos.obse ?? '',
            creadaEn: t,
            actualizadaEn: t,
        };
        await this.almacen.poner('lineas', linea);
        await this.tocar(capturaId);
        return linea;
    }
    async actualizarLinea(linea) {
        await this.almacen.poner('lineas', { ...linea, actualizadaEn: ahora() });
        await this.tocar(linea.capturaId);
    }
    async borrarLinea(linea) {
        await this.almacen.borrar('lineas', linea.id);
        await this.tocar(linea.capturaId);
    }
    async tocar(capturaId) {
        const c = await this.almacen.obtener('capturas', capturaId);
        if (c)
            await this.almacen.poner('capturas', { ...c, actualizadaEn: ahora() });
    }
    /**
     * Cierra la cama y la encola. El identificador del item de la bandeja es el de la captura:
     * reintentar dos veces no puede crear dos envíos (CN-24, idempotencia).
     */
    async cerrarCaptura(capturaId) {
        const c = await this.almacen.obtener('capturas', capturaId);
        if (!c)
            return undefined;
        const t = ahora();
        const cerrada = { ...c, estado: 'cerrada', cerradaEn: t, actualizadaEn: t };
        await this.almacen.poner('capturas', cerrada);
        const yaEncolado = await this.almacen.obtener('outbox', capturaId);
        if (!yaEncolado) {
            const item = {
                id: capturaId,
                capturaId,
                estado: 'pendiente',
                intentos: 0,
                creadoEn: t,
                ultimoError: null,
            };
            await this.almacen.poner('outbox', item);
        }
        return cerrada;
    }
    /** Reabre una captura cerrada. CNF: corregir antes de que se sincronice tiene que ser posible. */
    async reabrirCaptura(capturaId) {
        const c = await this.almacen.obtener('capturas', capturaId);
        if (!c)
            return;
        await this.almacen.poner('capturas', { ...c, estado: 'borrador', cerradaEn: null, actualizadaEn: ahora() });
        const item = await this.almacen.obtener('outbox', capturaId);
        if (item && item.estado === 'pendiente')
            await this.almacen.borrar('outbox', capturaId);
    }
    async lineasDe(capturaId) {
        const lineas = await this.almacen.porIndice('lineas', 'porCaptura', capturaId);
        return lineas.sort((a, b) => a.id.localeCompare(b.id)); // uuid v7: orden de captura
    }
    async capturasDeCama(camaId) {
        const capturas = await this.almacen.porIndice('capturas', 'porCama', camaId);
        capturas.sort((a, b) => b.id.localeCompare(a.id)); // la más reciente primero
        return Promise.all(capturas.map(async (captura) => ({ captura, lineas: await this.lineasDe(captura.id) })));
    }
    async resumen() {
        const [capturas, lineas, borradores, pendientes] = await Promise.all([
            this.almacen.contar('capturas'),
            this.almacen.contar('lineas'),
            this.almacen.porIndice('capturas', 'porEstado', 'borrador'),
            this.almacen.porIndice('outbox', 'porEstado', 'pendiente'),
        ]);
        return { capturas, lineas, borradores: borradores.length, pendientes: pendientes.length };
    }
    // -- instrumentación (D8) ------------------------------------------------------
    /**
     * Anota un evento. Nunca falla hacia afuera: si la instrumentación se rompe, la captura sigue.
     * Es una medición, no una funcionalidad.
     */
    async anotar(tipo, datos = {}) {
        try {
            const evento = {
                id: uuidv7(),
                tipo,
                ts: ahora(),
                capturaId: datos.capturaId ?? null,
                camaId: datos.camaId ?? null,
                variante: datos.variante ?? null,
                detalle: datos.detalle ?? '',
            };
            await this.almacen.poner('eventos', evento);
        }
        catch {
            /* medir nunca puede tumbar la captura */
        }
    }
    eventos = () => this.almacen.todos('eventos');
    // -- servidor simulado y conflictos (D6) ---------------------------------------
    registrosServidor = () => this.almacen.todos('servidor');
    conflictos = () => this.almacen.todos('conflictos');
    outbox = () => this.almacen.todos('outbox');
    async capturaPorId(id) {
        return this.almacen.obtener('capturas', id);
    }
    async guardarItemOutbox(item) {
        await this.almacen.poner('outbox', item);
    }
    async guardarEnServidor(registro) {
        await this.almacen.poner('servidor', registro);
    }
    async guardarConflicto(conflicto) {
        await this.almacen.poner('conflictos', conflicto);
    }
    async registroServidorDe(camaId, fecha) {
        const todos = await this.almacen.porIndice('servidor', 'porCamaFecha', [camaId, fecha]);
        return todos[0];
    }
    /** Cuántas capturas cerradas hay ya de esa cama ese día, sin contar la que se está mirando. */
    async otrasCapturasDelDia(camaId, fecha, exceptoId) {
        const capturas = await this.almacen.porIndice('capturas', 'porCama', camaId);
        return capturas.filter((c) => c.fecha === fecha && c.id !== exceptoId).length;
    }
    /** Solo para la demo: deja el dispositivo como recién instalado, sin borrar el catálogo. */
    async borrarCapturas() {
        await this.almacen.vaciar(['capturas', 'lineas', 'outbox', 'eventos', 'servidor', 'conflictos']);
    }
}
