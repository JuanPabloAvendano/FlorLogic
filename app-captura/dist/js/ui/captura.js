/**
 * Captura de una cama, en dos variantes.
 *
 * D3 · rejilla — la estructura del formato de papel. CNF: «captura con una plantilla predefinida
 *      que use una estructura similar a las plantillas de papel utilizadas previamente» = SÍ,
 *      y «progresiva y guiada pantalla por pantalla» = NO.
 * D7 · guiada — una sección a la vez, botones grandes, una sola mano. UXP: «con guantes puestos»,
 *      «bajo el sol directo», «mínimos toques», «escanear una marca física».
 *
 * Las dos escriben en el mismo repositorio y se miden igual. Están las dos porque las respuestas
 * de Juan se contradicen entre sí (contradicción 2 del plan) y esa pelea no se gana discutiendo:
 * se gana cronometrando al cliente con las dos.
 */
import { MotorReglas } from '../reglas.js';
import { aviso, boton, campo, entradaNumero, etiqueta, h, selector, tarjeta, titulo, vaciar, vacio } from '../vista.js';
import { escanearCodigo, hayEscaner } from '../escaner.js';
import { hoy, NOMBRE_VARIANTE, numero } from './comun.js';
class Sesion {
    ctx;
    captura;
    lineas;
    otrasHoy;
    constructor(ctx, captura, lineas, otrasHoy) {
        this.ctx = ctx;
        this.captura = captura;
        this.lineas = lineas;
        this.otrasHoy = otrasHoy;
    }
    variedadesDelBloque() {
        const bloque = this.ctx.catalogo.bloque(this.captura.bloqueId);
        const tipos = (bloque?.tiposFlor ?? []).filter(Boolean);
        const todas = this.ctx.catalogo.semilla.variedades;
        const propias = todas.filter((v) => tipos.includes(v.tipoFlor));
        return propias.length > 0 ? propias : todas;
    }
    async agregar(variedadId) {
        const linea = await this.ctx.repo.agregarLinea(this.captura.id, { variedadId, lineas: null, cantidad: null });
        this.lineas = [...this.lineas, linea];
        await this.ctx.repo.anotar('seccion.agregada', {
            capturaId: this.captura.id, camaId: this.captura.camaId, detalle: variedadId,
        });
        return linea;
    }
    async actualizar(linea, cambios) {
        const nueva = { ...linea, ...cambios };
        this.lineas = this.lineas.map((l) => (l.id === linea.id ? nueva : l));
        await this.ctx.repo.actualizarLinea(nueva);
        return nueva;
    }
    async quitar(linea) {
        this.lineas = this.lineas.filter((l) => l.id !== linea.id);
        await this.ctx.repo.borrarLinea(linea);
        await this.ctx.repo.anotar('seccion.quitada', { capturaId: this.captura.id, camaId: this.captura.camaId });
    }
    hallazgosDe(linea) {
        return this.ctx.motor.revisarLinea(linea, this.ctx.catalogo.variedad(linea.variedadId));
    }
    revisarTodo() {
        return this.ctx.motor.revisarCaptura({
            lineas: this.lineas,
            variedadPorId: (id) => this.ctx.catalogo.variedad(id),
            otrasCapturasHoy: this.otrasHoy,
        });
    }
    total() {
        return this.lineas.reduce((t, l) => t + (l.cantidad ?? 0), 0);
    }
    sugerencia(linea) {
        const v = this.ctx.catalogo.variedad(linea.variedadId);
        return linea.lineas && v?.plantasPorLinea ? linea.lineas * v.plantasPorLinea : null;
    }
}
export async function pantallaCaptura(ctx, camaId, variante) {
    const cama = ctx.catalogo.cama(camaId);
    if (!cama)
        return vacio('Cama no encontrada', 'El catálogo local no la tiene.');
    const bloque = ctx.catalogo.bloque(cama.bloqueId);
    const existentes = await ctx.repo.capturasDeCama(camaId);
    const abierta = existentes.find((c) => c.captura.estado === 'borrador');
    const captura = abierta?.captura ?? await ctx.repo.crearCaptura({
        camaId, bloqueId: cama.bloqueId, plantillaId: 'novedad_siembra', fecha: hoy(), usuario: ctx.usuario,
    });
    if (!abierta) {
        // El cronómetro de la medida de respuesta arranca aquí.
        await ctx.repo.anotar('captura.inicio', { capturaId: captura.id, camaId, variante });
    }
    ctx.capturaActiva.id = captura.id;
    ctx.capturaActiva.camaId = camaId;
    ctx.capturaActiva.variante = variante;
    const otrasHoy = await ctx.repo.otrasCapturasDelDia(camaId, captura.fecha, captura.id);
    const sesion = new Sesion(ctx, captura, abierta?.lineas ?? [], otrasHoy);
    const raiz = h('div', {});
    raiz.append(boton('‹ Cama ' + cama.codigo, () => ctx.ir({ n: 'cama', camaId }), 'tenue'));
    raiz.append(titulo(`Capturar cama ${cama.codigo}`, `Bloque ${bloque?.codigo ?? '—'} · ${captura.fecha} · ${NOMBRE_VARIANTE[variante]}`));
    const cuerpo = h('div', {});
    raiz.append(cuerpo);
    const pintar = () => {
        vaciar(cuerpo);
        cuerpo.append(variante === 'rejilla' ? rejilla(ctx, sesion, pintar, cerrar) : guiada(ctx, sesion, pintar, cerrar));
    };
    const cerrar = () => {
        vaciar(cuerpo);
        cuerpo.append(confirmacion(ctx, sesion, pintar));
    };
    pintar();
    return raiz;
}
// --- variante A: rejilla como el papel --------------------------------------------
function rejilla(ctx, sesion, pintar, cerrar) {
    const caja = h('section', { clase: 'captura' });
    caja.append(h('div', { clase: 'captura__cabeza' }, etiqueta('aviso', 'borrador'), h('span', { clase: 'captura__guardado', texto: 'Se guarda solo, dato por dato' })));
    const pie = pieDeCaptura(sesion, cerrar);
    const rejillaEl = h('div', { clase: 'rejilla' });
    rejillaEl.append(h('div', { clase: 'rejilla__cabecera' }, h('span', { texto: 'VARIEDAD' }), h('span', { texto: '# LÍNEAS' }), h('span', { texto: 'CANTIDAD' }), h('span', { texto: '' })));
    for (const linea of sesion.lineas)
        rejillaEl.append(filaRejilla(ctx, sesion, linea, pintar, pie.refrescar));
    caja.append(rejillaEl);
    if (sesion.lineas.length === 0) {
        caja.append(h('p', { clase: 'nota', texto: 'Todavía no hay secciones. En el formato real, tres de las camas del bloque 12 tienen dos.' }));
    }
    const variedades = sesion.variedadesDelBloque();
    caja.append(h('div', { clase: 'acciones' }, boton('+ Sección', async () => {
        await sesion.agregar(variedades[0]?.id ?? '');
        pintar();
    }), 
    // UXP: «deshacer el último dato registrado sin salir de la pantalla».
    sesion.lineas.length > 0
        ? boton('Deshacer última', async () => {
            const ultima = sesion.lineas[sesion.lineas.length - 1];
            if (ultima)
                await sesion.quitar(ultima);
            pintar();
        }, 'tenue')
        : null));
    caja.append(pie.el);
    return caja;
}
function filaRejilla(ctx, sesion, lineaInicial, pintar, refrescarPie) {
    let linea = lineaInicial;
    const fila = h('div', { clase: 'rejilla__fila' });
    const avisos = h('div', { clase: 'rejilla__avisos' });
    const repintarAvisos = () => {
        vaciar(avisos);
        for (const hz of sesion.hallazgosDe(linea)) {
            avisos.append(aviso(hz.severidad === 'dura' ? 'alerta' : 'aviso', hz.mensaje, hz.queHacer));
        }
        const s = sesion.sugerencia(linea);
        if (s !== null && linea.cantidad === null) {
            // CN-31: la sugerencia se ve, nunca se escribe sola.
            avisos.append(h('p', { clase: 'aviso aviso--neutro' }, `Con ${ctx.catalogo.variedad(linea.variedadId)?.plantasPorLinea} por línea serían ${numero(s)}. `, boton('Usar', async () => {
                linea = await sesion.actualizar(linea, { cantidad: s });
                await ctx.repo.anotar('sugerencia.usada', { capturaId: linea.capturaId, camaId: sesion.captura.camaId });
                pintar();
            }, 'tenue')));
        }
    };
    fila.append(selector(sesion.variedadesDelBloque().map((v) => ({ valor: v.id, texto: v.nombre })), linea.variedadId, async (v) => { linea = await sesion.actualizar(linea, { variedadId: v }); repintarAvisos(); refrescarPie(); }));
    fila.append(entradaNumero(linea.lineas === null ? '' : String(linea.lineas), async (v) => {
        linea = await sesion.actualizar(linea, { lineas: v === '' ? null : Number(v) });
        repintarAvisos();
        refrescarPie();
    }));
    fila.append(entradaNumero(linea.cantidad === null ? '' : String(linea.cantidad), async (v) => {
        linea = await sesion.actualizar(linea, { cantidad: v === '' ? null : Number(v) });
        repintarAvisos();
        refrescarPie();
    }));
    fila.append(boton('×', async () => { await sesion.quitar(linea); pintar(); }, 'tenue'));
    repintarAvisos();
    return h('div', { clase: 'rejilla__grupo' }, fila, avisos);
}
// --- variante B: guiada, una sección a la vez -------------------------------------
function guiada(ctx, sesion, pintar, cerrar) {
    const caja = h('section', { clase: 'captura' });
    caja.append(h('div', { clase: 'captura__cabeza' }, etiqueta('aviso', 'borrador'), h('span', { clase: 'captura__guardado', texto: 'Se guarda solo, dato por dato' })));
    if (hayEscaner()) {
        caja.append(h('div', { clase: 'acciones' }, boton('Escanear la marca de la cama', async () => {
            const codigo = await escanearCodigo(caja);
            if (!codigo)
                return;
            const destino = ctx.catalogo.semilla.camas.find((c) => c.codigo === codigo || c.id === codigo);
            if (destino)
                ctx.ir({ n: 'captura', camaId: destino.id, variante: 'guiada' });
            else
                caja.append(aviso('aviso', `No hay ninguna cama con el código ${codigo}.`, 'Buscala en la lista de bloques.'));
        }, 'tenue')));
    }
    for (const linea of sesion.lineas) {
        const v = ctx.catalogo.variedad(linea.variedadId);
        const completa = linea.lineas !== null && linea.cantidad !== null;
        caja.append(h('div', { clase: 'seccion' }, h('div', { clase: 'seccion__cabeza' }, h('span', { clase: 'seccion__variedad', texto: v?.nombre ?? '—' }), completa ? etiqueta('ok', 'lista') : etiqueta('aviso', 'incompleta')), h('dl', { clase: 'datos' }, h('dt', { texto: '# líneas' }), h('dd', { texto: String(linea.lineas ?? '—') }), h('dt', { texto: 'Cantidad' }), h('dd', { texto: numero(linea.cantidad) })), h('div', { clase: 'acciones' }, boton('Quitar', async () => { await sesion.quitar(linea); pintar(); }, 'tenue'))));
    }
    caja.append(formularioGuiado(ctx, sesion, pintar));
    caja.append(pieDeCaptura(sesion, cerrar).el);
    return caja;
}
function formularioGuiado(ctx, sesion, pintar) {
    const variedades = sesion.variedadesDelBloque();
    let variedadId = variedades[0]?.id ?? '';
    let lineasTexto = '';
    let cantidadTexto = '';
    const sugerenciaEl = h('div', {});
    const refrescarSugerencia = () => {
        vaciar(sugerenciaEl);
        const v = ctx.catalogo.variedad(variedadId);
        const n = lineasTexto === '' ? null : Number(lineasTexto);
        if (n && v?.plantasPorLinea && cantidadTexto === '') {
            const s = n * v.plantasPorLinea;
            sugerenciaEl.append(h('p', { clase: 'aviso aviso--neutro' }, `Con ${v.plantasPorLinea} por línea serían ${numero(s)}. `, boton('Usar', () => { cantidadTexto = String(s); entradaCantidad.value = cantidadTexto; refrescarSugerencia(); }, 'tenue'), v.confianza === 'en-disputa' ? etiqueta('alerta', 'valor no confiable') : null));
        }
    };
    // ACC: botones grandes en vez de una lista desplegable cuando hay pocas variedades.
    const botonera = h('div', { clase: 'botonera' });
    const pintarBotonera = () => {
        vaciar(botonera);
        for (const v of variedades.slice(0, 8)) {
            const b = boton(v.nombre, () => { variedadId = v.id; pintarBotonera(); refrescarSugerencia(); }, v.id === variedadId ? 'normal' : 'tenue');
            botonera.append(b);
        }
    };
    pintarBotonera();
    const entradaLineas = entradaNumero('', (v) => { lineasTexto = v; refrescarSugerencia(); });
    const entradaCantidad = entradaNumero('', (v) => { cantidadTexto = v; });
    return h('div', { clase: 'formulario' }, campo('Variedad', botonera), campo('# Líneas', entradaLineas), campo('Cantidad', entradaCantidad), sugerenciaEl, h('div', { clase: 'acciones' }, boton('Agregar sección', async () => {
        if (!variedadId)
            return;
        const linea = await sesion.agregar(variedadId);
        await sesion.actualizar(linea, {
            lineas: lineasTexto === '' ? null : Number(lineasTexto),
            cantidad: cantidadTexto === '' ? null : Number(cantidadTexto),
        });
        pintar();
    })));
}
// --- pie y confirmación -----------------------------------------------------------
/**
 * UXP: «ver en todo momento cuánto se lleva capturado». El total se recalcula con cada tecla,
 * no al pintar la pantalla: si se queda quieto mientras se escribe, deja de servir.
 */
function pieDeCaptura(sesion, cerrar) {
    const total = h('div', { clase: 'pie-captura__total' });
    const refrescar = () => {
        vaciar(total);
        total.append(h('span', { texto: `${sesion.lineas.length} ${sesion.lineas.length === 1 ? 'sección' : 'secciones'}` }), h('strong', { texto: `${numero(sesion.total())} plantas` }));
    };
    refrescar();
    return { el: h('div', { clase: 'pie-captura' }, total, boton('Cerrar cama', cerrar)), refrescar };
}
/**
 * CNF: «una confirmación final que muestre todo lo capturado antes de darlo por guardado» = SÍ.
 * Las reglas duras impiden cerrar; las blandas exigen que alguien las mire y siga a propósito.
 */
function confirmacion(ctx, sesion, volver) {
    const hallazgos = sesion.revisarTodo();
    const duras = MotorReglas.bloquean(hallazgos);
    const blandas = hallazgos.filter((hz) => hz.severidad === 'blanda');
    const caja = h('section', { clase: 'captura' });
    caja.append(h('h2', { clase: 'seccion__titulo', texto: 'Confirmá antes de guardar' }));
    const resumen = h('div', { clase: 'lista lista--densa' });
    for (const l of sesion.lineas) {
        resumen.append(tarjeta(null, h('div', { clase: 'tarjeta__fila' }, h('span', { clase: 'tarjeta__codigo', texto: ctx.catalogo.variedad(l.variedadId)?.nombre ?? '—' }), h('strong', { texto: numero(l.cantidad) })), h('div', { clase: 'tarjeta__meta' }, h('span', { texto: `${l.lineas ?? '—'} líneas` }))));
    }
    caja.append(resumen);
    caja.append(h('p', { clase: 'total-final' }, `Total de la cama: `, h('strong', { texto: numero(sesion.total()) }), ' plantas'));
    for (const hz of duras)
        caja.append(aviso('alerta', hz.mensaje, hz.queHacer));
    for (const hz of blandas)
        caja.append(aviso('aviso', hz.mensaje, hz.queHacer));
    let revisado = blandas.length === 0;
    if (blandas.length > 0) {
        const check = h('input', { tipo: 'checkbox' });
        check.addEventListener('change', () => { revisado = check.checked; refrescarBoton(); });
        caja.append(h('label', { clase: 'confirmar' }, check, ' Lo revisé y quiero cerrar la cama así.'));
    }
    const botonCerrar = boton('Guardar y cerrar', async () => {
        if (duras.length > 0 || !revisado)
            return;
        await ctx.repo.cerrarCaptura(sesion.captura.id);
        await ctx.repo.anotar('captura.cierre', {
            capturaId: sesion.captura.id,
            camaId: sesion.captura.camaId,
            variante: ctx.capturaActiva.variante,
            detalle: `${sesion.lineas.length} secciones · ${sesion.total()} plantas`,
        });
        ctx.capturaActiva.id = null;
        ctx.refrescarShell();
        ctx.ir({ n: 'cama', camaId: sesion.captura.camaId });
    });
    const refrescarBoton = () => {
        const puede = duras.length === 0 && revisado;
        if (puede)
            botonCerrar.removeAttribute('disabled');
        else
            botonCerrar.setAttribute('disabled', 'true');
    };
    if (duras.length > 0) {
        void ctx.repo.anotar('regla.rechazo', {
            capturaId: sesion.captura.id,
            camaId: sesion.captura.camaId,
            variante: ctx.capturaActiva.variante,
            detalle: duras.map((d) => d.reglaId).join(','),
        });
    }
    caja.append(h('div', { clase: 'acciones' }, botonCerrar, boton('Seguir capturando', volver, 'tenue')));
    refrescarBoton();
    return caja;
}
