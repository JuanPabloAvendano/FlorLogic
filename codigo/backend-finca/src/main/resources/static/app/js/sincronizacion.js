/**
 * Sincronización con el nodo de la finca (TX-1). En la demo el «servidor» vivía en el mismo celular; ahora del otro
 * lado está el nodo de verdad, por HTTPS y con la sesión de Keycloak, y esta es la pieza que cambió (lo decía la demo:
 * «cuando exista el backend, esta es la única pieza que se cambia»).
 *
 * Una sincronización es una sesión del nodo (CT-1): abrirla, entregar cada cama pendiente como una partición, cerrarla.
 * Solo usa lo que el nodo ya hace:
 *  - CN-24 · idempotente: cada cama se fija una vez (consecutivos y bytes) y un reintento manda lo mismo; el nodo
 *    cuenta `sin_efecto` lo que ya tenía.
 *  - CN-24 · cronológica: la bandeja se procesa en orden de UUID v7, y los consecutivos siguen ese orden.
 *  - Lo que choca no se media en el celular: el nodo aplica la captura más reciente (RF-022) y lo marcado le abre un
 *    trabajo al gerente. La mediación de la demo (DEC-05, «decide una persona») queda por decidir con Juan.
 *
 * IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR.
 */
import { uuidv7 } from './id.js';
import { describir } from './nodo.js';
import { armarParticion, VERSION_CONFIG } from './particion.js';

/**
 * @param d.repo       el repositorio local (bandeja, capturas, consecutivos)
 * @param d.nodo       el cliente HTTP del nodo (NodoDeFinca)
 * @param d.credencial `{ token(): Promise<string|null>, persona(): {sub, jti} | null, olvidar() }`
 * @param d.catalogo   para el código del bloque y de la cama
 * @param d.motor      versión del motor de reglas local
 * @param d.uuid       generador de UUID v7 (las pruebas lo fijan)
 * @param d.ahora      reloj de pared en ms (las pruebas lo fijan)
 */
export async function sincronizar({ repo, nodo, credencial, catalogo, motor, uuid = uuidv7, ahora = Date.now }) {
    const salida = { entregadas: 0, pendientes: 0, necesitaEntrar: false, detalle: [], cierre: null };
    const pendientes = (await repo.outbox())
        .filter((i) => i.estado === 'pendiente')
        .sort((a, b) => a.id.localeCompare(b.id)); // UUID v7 = orden cronológico
    salida.pendientes = pendientes.length;
    if (pendientes.length === 0) {
        salida.detalle.push('No hay camas cerradas sin entregar.');
        return salida;
    }

    let token;
    try {
        token = await credencial.token();
    }
    catch {
        salida.detalle.push('Sin conexión con el nodo: nada se perdió, queda todo en la bandeja.');
        return salida;
    }
    if (!token) {
        salida.necesitaEntrar = true;
        salida.detalle.push('Para entregar hay que entrar con tu usuario de operador.');
        return salida;
    }

    const abierta = await nodo.abrir(token, { dispositivo: repo.dispositivo(), relojMs: ahora(), versionConfig: VERSION_CONFIG });
    if (abierta.estado !== 201) {
        return parar(salida, abierta, credencial);
    }
    const sesion = abierta.datos.sesion_uuid;
    if (abierta.datos.marcada) {
        salida.detalle.push('Este celular no está aprobado en el nodo: lo que entregue entra marcado hasta que el '
            + 'administrador lo apruebe en Celulares.');
    }

    let completa = true;
    for (const pendiente of pendientes) {
        let item;
        try {
            item = await fijar(repo, pendiente, { catalogo, motor, credencial, desfase: abierta.datos.desfase_ms, uuid });
        }
        catch (e) {
            // No se pudo armar (por ejemplo, más datos de los que caben): no gastó consecutivos y las demás siguen.
            completa = false;
            await repo.guardarItemOutbox({ ...pendiente, ultimoError: e instanceof Error ? e.message : String(e) });
            continue;
        }
        if (!item) {
            salida.pendientes -= 1; // la captura ya no existe: sale de la bandeja
            continue;
        }
        const r = await nodo.entregar(token, sesion, item.particion);
        if (r.estado === 200) {
            await repo.guardarItemOutbox({
                ...item, estado: 'enviado', intentos: item.intentos + 1, ultimoError: null, resultado: resultado(r.datos),
            });
            await repo.anotar('sincronizacion', { capturaId: item.capturaId, detalle: 'entregada' });
            salida.entregadas += 1;
            salida.pendientes -= 1;
            continue;
        }
        completa = false;
        await repo.guardarItemOutbox({ ...item, intentos: item.intentos + 1, ultimoError: describir(r) });
        await repo.anotar('sincronizacion', { capturaId: item.capturaId, detalle: `respuesta ${r.estado}` });
        if (r.estado === 422) {
            continue; // ésta no entra; las demás sí, y el nodo espera sus consecutivos
        }
        parar(salida, r, credencial);
        break; // sin red, sin sesión o sin permiso: lo que sigue tampoco entraría
    }

    const cerrada = await nodo.cerrar(token, sesion, completa);
    if (cerrada.estado === 200) {
        salida.cierre = {
            en: new Date(ahora()).toISOString(),
            estado: cerrada.datos.estado,
            confirmadoHasta: cerrada.datos.confirmado_hasta,
            avisos: cerrada.datos.avisos ?? [],
        };
        await repo.guardarUltimaSincronizacion(salida.cierre);
    }
    salida.detalle.unshift(`${salida.entregadas} entregada(s), ${salida.pendientes} en la bandeja.`);
    return salida;
}

/** Fija el envío de una captura (consecutivos y bytes) si todavía no lo estaba. `undefined` si la captura ya no existe. */
async function fijar(repo, item, { catalogo, motor, credencial, desfase, uuid }) {
    if (item.particion) {
        return item;
    }
    const captura = await repo.capturaPorId(item.capturaId);
    if (!captura) {
        await repo.guardarItemOutbox({ ...item, estado: 'enviado', ultimoError: 'la captura ya no existe' });
        return undefined;
    }
    const lineas = await repo.lineasDe(captura.id);
    const cama = catalogo.cama(captura.camaId);
    const bloque = catalogo.bloque(captura.bloqueId ?? cama?.bloqueId);
    const persona = credencial.persona();
    return repo.fijarEnvio(item.id, (desde) => armarParticion({
        captura, lineas, bloque, cama, persona, motor, desde, desfase, uuid,
    }));
}

/** Lo que el nodo dijo de una partición, para la bandeja. */
function resultado(datos) {
    return {
        confirmadoHasta: datos.confirmado_hasta,
        conteo: datos.conteo,
        excepciones: (datos.excepciones ?? []).map((e) => `${e.resultado}: ${(e.codigos ?? []).join(', ')}`),
    };
}

function parar(salida, respuesta, credencial) {
    if (respuesta.estado === 401) {
        credencial.olvidar();
        salida.necesitaEntrar = true;
    }
    salida.detalle.push(describir(respuesta));
    return salida;
}

export function resumenOutbox(items) {
    const enviados = items.filter((i) => i.estado === 'enviado');
    return {
        pendientes: items.filter((i) => i.estado === 'pendiente').length,
        enviados: enviados.length,
        conObservaciones: enviados.filter((i) => (i.resultado?.excepciones ?? []).length > 0).length,
    };
}
