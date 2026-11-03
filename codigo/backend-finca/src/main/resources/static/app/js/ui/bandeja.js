/**
 * D6 · bandeja de salida, ahora hacia el nodo de la finca (TX-1).
 *
 * Lo que hay que poder enseñar aquí sigue siendo el comportamiento:
 *  - lo capturado se queda en el dispositivo y se ve que se queda, con o sin señal;
 *  - reintentar no duplica (CN-24): el nodo cuenta `sin_efecto` lo que ya tenía;
 *  - lo que el nodo dijo de cada cama (aplicadas, marcadas, rechazadas) y hasta qué consecutivo confirmó.
 *
 * Entregar pide la sesión del operador (Keycloak, cliente `celular`); capturar no. Lo marcado lo resuelve el gerente
 * en la web del nodo, no el celular.
 *
 * IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR (antes: la bandeja de la demo con servidor simulado).
 */
import { entrar, olvidar, salir, sesion, tokenVigente } from '../auth.js';
import { NodoDeFinca } from '../nodo.js';
import { resumenOutbox, sincronizar } from '../sincronizacion.js';
import { aviso, boton, cifra, etiqueta, h, tabla, titulo, vaciar, vacio } from '../vista.js';
const nodo = new NodoDeFinca();
const credencial = {
    token: tokenVigente,
    persona: sesion,
    olvidar,
};
export async function pantallaBandeja(ctx) {
    const raiz = h('div', {});
    raiz.append(titulo('Bandeja de salida', 'Lo capturado que todavía no está en el nodo de la finca'));
    const cuerpo = h('div', {});
    raiz.append(cuerpo);
    const pintar = async () => {
        vaciar(cuerpo);
        const items = await ctx.repo.outbox();
        const r = resumenOutbox(items);
        const ultima = await ctx.repo.ultimaSincronizacion();
        const quien = sesion();
        cuerpo.append(h('div', { clase: 'cifras' }, cifra(r.pendientes, 'sin entregar'), cifra(r.enviados, 'entregadas'), cifra(r.conObservaciones, 'con observaciones'), cifra(ultima?.confirmadoHasta ?? 0, 'confirmado hasta')));
        cuerpo.append(h('p', { clase: 'nota' }, quien ? `Entregás como ${quien.usuario}. ` : 'Sin sesión: para entregar hay que entrar. ', `Este celular: ${ctx.repo.dispositivo()}`));
        cuerpo.append(h('div', { clase: 'acciones' }, boton('Sincronizar', async () => {
            const res = await sincronizar({ repo: ctx.repo, nodo, credencial, catalogo: ctx.catalogo, motor: ctx.motor.version() });
            if (res.necesitaEntrar) {
                await entrar({ n: 'bandeja' });
                return;
            }
            await pintar();
            ctx.refrescarShell();
            const tono = res.pendientes === 0 ? 'ok' : 'aviso';
            cuerpo.prepend(aviso(tono, res.detalle[0] ?? '', res.detalle.slice(1).join(' ')));
        }), quien
            ? boton('Salir', () => salir(), 'tenue')
            : boton('Entrar', () => entrar({ n: 'bandeja' }), 'tenue')));
        if (ultima) {
            cuerpo.append(aviso(String(ultima.estado).toUpperCase() === 'CERRADA' ? 'neutro' : 'aviso',`Última sincronización: ${new Date(ultima.en).toLocaleString('es-CO')} · sesión ${String(ultima.estado).toLowerCase()} · el nodo confirmó hasta el consecutivo ${ultima.confirmadoHasta}.`, (ultima.avisos ?? []).join(' · ')));
        }
        cuerpo.append(h('h2', { clase: 'seccion__titulo', texto: 'Envíos' }));
        if (items.length === 0) {
            cuerpo.append(vacio('La bandeja está vacía', 'Cerrá una cama y aparece acá.'));
            return;
        }
        const filas = await Promise.all(items
            .sort((a, b) => b.id.localeCompare(a.id))
            .map(async (i) => {
            const captura = await ctx.repo.capturaPorId(i.capturaId);
            const cama = captura ? ctx.catalogo.cama(captura.camaId) : undefined;
            const observaciones = i.resultado?.excepciones ?? [];
            const tono = i.estado !== 'enviado' ? 'aviso' : observaciones.length > 0 ? 'alerta' : 'ok';
            return [
                `Cama ${cama?.codigo ?? '—'}`,
                captura?.fecha ?? '—',
                etiqueta(tono, i.estado === 'enviado' ? 'entregada' : 'sin entregar'),
                i.consecutivos ? `${i.consecutivos[0]}–${i.consecutivos[1]}` : '—',
                String(i.intentos),
                i.ultimoError ?? (i.resultado ? lectura(i.resultado) : ''),
            ];
        }));
        cuerpo.append(tabla(['Cama', 'Fecha', 'Estado', 'Consecutivos', 'Intentos', 'Lo que dijo el nodo'], filas));
    };
    await pintar();
    return raiz;
}
function lectura(resultado) {
    const c = resultado.conteo ?? {};
    const partes = [`${c.aplicadas ?? 0} aplicadas`];
    if (c.marcadas)
        partes.push(`${c.marcadas} marcadas`);
    if (c.rechazadas)
        partes.push(`${c.rechazadas} rechazadas`);
    if (c.sin_efecto)
        partes.push(`${c.sin_efecto} sin efecto`);
    const codigos = [...new Set(resultado.excepciones.map((e) => e.split(': ')[1]).filter(Boolean))];
    return partes.join(', ') + (codigos.length ? ` (${codigos.join('; ')})` : '');
}
