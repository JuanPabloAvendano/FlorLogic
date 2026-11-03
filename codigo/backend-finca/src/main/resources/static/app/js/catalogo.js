/**
 * Índice en memoria de la semilla.
 *
 * D1 solo lee. La persistencia local (Dexie) y la captura entran en el D2 del plan;
 * por eso aquí no hay ninguna escritura y el índice se puede botar y reconstruir.
 */
export class Catalogo {
    semilla;
    camasPorBloque = new Map();
    siembrasPorCama = new Map();
    variedadPorId = new Map();
    anomaliasPorSiembra = new Map();
    constructor(semilla) {
        this.semilla = semilla;
        for (const c of semilla.camas) {
            const lista = this.camasPorBloque.get(c.bloqueId) ?? [];
            lista.push(c);
            this.camasPorBloque.set(c.bloqueId, lista);
        }
        for (const s of semilla.siembras) {
            const lista = this.siembrasPorCama.get(s.camaId) ?? [];
            lista.push(s);
            this.siembrasPorCama.set(s.camaId, lista);
        }
        for (const v of semilla.variedades)
            this.variedadPorId.set(v.id, v);
        for (const a of semilla.anomalias) {
            if (!a.siembraId)
                continue;
            const lista = this.anomaliasPorSiembra.get(a.siembraId) ?? [];
            lista.push(a);
            this.anomaliasPorSiembra.set(a.siembraId, lista);
        }
    }
    bloques() { return this.semilla.bloques; }
    camas(bloqueId) { return this.camasPorBloque.get(bloqueId) ?? []; }
    siembras(camaId) { return this.siembrasPorCama.get(camaId) ?? []; }
    variedad(id) { return this.variedadPorId.get(id); }
    anomalias(siembraId) { return this.anomaliasPorSiembra.get(siembraId) ?? []; }
    bloque(id) { return this.semilla.bloques.find((b) => b.id === id); }
    cama(id) { return this.semilla.camas.find((c) => c.id === id); }
    /** Una cama está dividida cuando el mismo día tiene más de una sección sembrada (DEC-14). */
    secciones(camaId, fecha) {
        return this.siembras(camaId).filter((s) => s.fecha === fecha);
    }
    estaDividida(camaId) {
        const porFecha = new Map();
        for (const s of this.siembras(camaId)) {
            porFecha.set(s.fecha, (porFecha.get(s.fecha) ?? 0) + 1);
        }
        return [...porFecha.values()].some((n) => n > 1);
    }
    plantasDeLaCama(camaId) {
        return this.siembras(camaId).reduce((total, s) => total + (s.cantidad ?? 0), 0);
    }
    resumen() {
        const revisar = this.semilla.anomalias.filter((a) => a.severidad === 'revisar').length;
        const divididas = this.semilla.camas.filter((c) => this.estaDividida(c.id)).length;
        return {
            bloques: this.semilla.bloques.length,
            camas: this.semilla.camas.length,
            variedades: this.semilla.variedades.length,
            siembras: this.semilla.siembras.length,
            revisar,
            divididas,
            porcentajeDivididas: this.semilla.camas.length
                ? Math.round((divididas / this.semilla.camas.length) * 100)
                : 0,
        };
    }
}
export async function cargarSemilla() {
    // `base: './'` en vite.config.ts hace que esto funcione igual en la raíz y en un subdirectorio.
    const url = new URL('seed.json', document.baseURI).toString();
    const res = await fetch(url, { cache: 'no-cache' });
    if (!res.ok)
        throw new Error(`No se pudo leer seed.json (${res.status})`);
    return (await res.json());
}
