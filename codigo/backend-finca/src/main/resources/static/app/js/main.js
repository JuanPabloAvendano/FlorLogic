/**
 * Arranque de la demo de captura de FlorLogic.
 *
 * Orden de encendido, que no es casual:
 *   1. se abre la base local del dispositivo;
 *   2. se instala y versiona el catálogo (CN-26: primero el catálogo, después capturar);
 *   3. se cargan las reglas desde su archivo (CN-22);
 *   4. recién ahí se pinta.
 *
 * A partir de la segunda vez todo sale de IndexedDB: `seed.json` es el instalador, no la fuente.
 *
 * Servida por el nodo de la finca en /app/ (F8, 3-nov-2026): si la página es el regreso de Keycloak, primero se termina
 * el inicio de sesión (auth.js) y se vuelve a la pantalla desde la que se pidió, normalmente la bandeja.
 */
import { alVolver, sesion } from './auth.js';
import { Catalogo, cargarSemilla } from './catalogo.js';
import { cargarReglas } from './reglas.js';
import { Repositorio } from './repositorio.js';
import { aviso, boton, etiqueta, h, vaciar, vacio } from './vista.js';
import { pantallaBandeja } from './ui/bandeja.js';
import { pantallaCamas, pantallaCama, pantallaInicio, pantallaPapel } from './ui/catalogoPantallas.js';
import { pantallaCaptura } from './ui/captura.js';
import { pantallaDatos } from './ui/datos.js';
import { pantallaMedidas } from './ui/medidas.js';
async function arrancar() {
    const raiz = document.getElementById('raiz');
    if (!raiz)
        return;
    raiz.append(vacio('Abriendo…', 'Instalando el catálogo en el dispositivo.'));
    let repo;
    let catalogo;
    let motor;
    try {
        repo = await Repositorio.abrir();
        await repo.sembrarCatalogo(await cargarSemilla());
        catalogo = new Catalogo(await repo.semillaLocal());
        motor = await cargarReglas();
    }
    catch (e) {
        vaciar(raiz);
        raiz.append(vacio('No se pudo abrir la aplicación', `${e instanceof Error ? e.message : String(e)}. Recargá la página; si sigue, abrí limpiar.html.`));
        return;
    }
    // El regreso de Keycloak trae ?code=…&state=…: se canjea antes de pintar y se vuelve a donde se estaba.
    let vista = { n: 'inicio' };
    let errorDeEntrada = null;
    try {
        const volverA = await alVolver();
        if (volverA !== undefined)
            vista = volverA ?? { n: 'bandeja' };
    }
    catch (e) {
        vista = { n: 'bandeja' };
        errorDeEntrada = e instanceof Error ? e.message : String(e);
    }
    const capturaActiva = { id: null, camaId: null, variante: null };
    const cabecera = h('header', { clase: 'cabecera' });
    const contenido = h('main', { clase: 'contenido' });
    const navegacion = h('nav', { clase: 'navegacion' });
    const pie = h('footer', { clase: 'pie' });
    const ctx = {
        repo, catalogo, motor,
        /** Quién captura: la persona de la última sesión en este celular. Capturar no pide sesión (CN-13). */
        get usuario() { return sesion()?.usuario ?? 'sin sesión'; },
        ir: (v) => { vista = v; void pintar(); },
        refrescarShell: () => { void pintarCabecera(); },
        vistaActual: () => vista,
        capturaActiva,
    };
    async function pintarCabecera() {
        const r = await repo.resumen();
        vaciar(cabecera);
        cabecera.append(h('button', { clase: 'cabecera__marca', tipo: 'button', onClick: () => ctx.ir({ n: 'inicio' }) }, h('strong', { texto: 'FlorLogic' }), h('span', { clase: 'cabecera__sub', texto: `Captura · ${catalogo.semilla.fincas[0]?.nombre ?? '—'}` })), h('div', { clase: 'cabecera__estado' }, 
        // DSP: la aplicación dice siempre si está trabajando sin conexión.
        etiqueta(navigator.onLine ? 'ok' : 'aviso', navigator.onLine ? 'En línea' : 'Sin conexión'), 
        // UXP: qué se sincronizó y qué no, siempre a la vista.
        r.pendientes > 0 ? etiqueta('aviso', `${r.pendientes} sin enviar`) : null, 
        // Quién entrega, si ya entró: la sesión se pide al sincronizar.
        sesion() ? etiqueta('neutro', sesion().usuario) : null));
    }
    function pintarNavegacion() {
        vaciar(navegacion);
        const items = [
            { texto: 'Bloques', destino: { n: 'inicio' }, activo: ['inicio', 'camas', 'cama', 'captura', 'papel'].includes(vista.n) },
            { texto: 'Bandeja', destino: { n: 'bandeja' }, activo: vista.n === 'bandeja' },
            { texto: 'Medidas', destino: { n: 'medidas' }, activo: vista.n === 'medidas' },
            { texto: 'Datos', destino: { n: 'datos' }, activo: vista.n === 'datos' },
        ];
        for (const i of items) {
            navegacion.append(boton(i.texto, () => ctx.ir(i.destino), i.activo ? 'normal' : 'tenue'));
        }
    }
    async function pintar() {
        if (vista.n !== 'captura')
            capturaActiva.id = null;
        pintarNavegacion();
        void pintarCabecera();
        vaciar(contenido);
        contenido.append(await construirPantalla());
        window.scrollTo(0, 0);
    }
    async function construirPantalla() {
        switch (vista.n) {
            case 'inicio': return pantallaInicio(ctx);
            case 'camas': return pantallaCamas(ctx, vista.bloqueId);
            case 'cama': return pantallaCama(ctx, vista.camaId);
            case 'captura': return pantallaCaptura(ctx, vista.camaId, vista.variante);
            case 'papel': return pantallaPapel(ctx, vista.camaId);
            case 'bandeja': return pantallaBandeja(ctx);
            case 'medidas': return pantallaMedidas(ctx);
            case 'datos': return pantallaDatos(ctx, async () => {
                await repo.borrarCapturas();
                await pintar();
            });
        }
    }
    vaciar(raiz);
    raiz.append(h('div', { clase: 'app' }, cabecera, contenido, navegacion, pie));
    pie.append(h('span', { texto: `${catalogo.semilla.version} · ${motor.version()} · ${catalogo.semilla.origen}` }));
    // D8 · contador de toques. Solo cuenta mientras hay una cama abierta, y nunca puede
    // interrumpir la captura: si falla, falla en silencio.
    contenido.addEventListener('pointerdown', () => {
        if (vista.n === 'captura' && capturaActiva.id) {
            void repo.anotar('toque', {
                capturaId: capturaActiva.id, camaId: capturaActiva.camaId, variante: capturaActiva.variante,
            });
        }
    });
    window.addEventListener('online', () => void pintarCabecera());
    window.addEventListener('offline', () => void pintarCabecera());
    await pintar();
    if (errorDeEntrada)
        contenido.prepend(aviso('alerta', errorDeEntrada, 'Volvé a intentarlo con Entrar.'));
}
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register(new URL('sw.js', document.baseURI).toString()).catch(() => {
            /* sin service worker la demo funciona igual, solo pierde el arranque sin conexión */
        });
    });
}
void arrancar();
