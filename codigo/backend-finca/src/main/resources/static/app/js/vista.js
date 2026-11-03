/**
 * Capa de vista mínima. Sin framework y sin dependencias.
 *
 * Por qué no React: el proyecto se construye para botarse (regla R2 del plan) y el precio de
 * React aquí no es el peso, es el `npm install` — que en el campo, con la conexión de una finca,
 * es exactamente el paso que falla. Con esto el proyecto entero se compila con `tsc` y se sirve
 * como archivos estáticos.
 *
 * No hay reconciliación: cada pantalla arma su DOM una vez y las partes que cambian se vuelven a
 * pintar a mano. Es más código en las listas, pero ningún campo pierde el foco mientras se escribe
 * — que con guantes y bajo el sol no es un detalle.
 */
export function h(etiqueta, props = {}, ...hijos) {
    const el = document.createElement(etiqueta);
    if (props.clase)
        el.className = props.clase;
    if (props.texto !== undefined)
        el.textContent = props.texto;
    if (props.onClick)
        el.addEventListener('click', props.onClick);
    if (props.onInput)
        el.addEventListener('input', props.onInput);
    if (props.onChange)
        el.addEventListener('change', props.onChange);
    if (props.tipo && 'type' in el)
        el.type = props.tipo;
    if (props.valor !== undefined && 'value' in el)
        el.value = props.valor;
    if (props.modoEntrada)
        el.setAttribute('inputmode', props.modoEntrada);
    if (props.marcador)
        el.setAttribute('placeholder', props.marcador);
    if (props.deshabilitado)
        el.setAttribute('disabled', 'true');
    for (const [k, v] of Object.entries(props.atributos ?? {}))
        el.setAttribute(k, v);
    for (const hijo of hijos) {
        if (hijo === null || hijo === undefined || hijo === false)
            continue;
        el.append(hijo instanceof Node ? hijo : document.createTextNode(String(hijo)));
    }
    return el;
}
export function vaciar(el) {
    while (el.firstChild)
        el.removeChild(el.firstChild);
}
const SIMBOLO = { ok: '✓', aviso: '!', alerta: '✕', neutro: '·' };
export function etiqueta(tono, texto) {
    return h('span', { clase: `etiqueta etiqueta--${tono}` }, h('span', { texto: SIMBOLO[tono], atributos: { 'aria-hidden': 'true' } }), ' ' + texto);
}
export function boton(texto, onClick, variante = 'normal') {
    return h('button', { clase: `boton boton--${variante}`, texto, tipo: 'button', onClick });
}
export function tarjeta(onClick, ...hijos) {
    return onClick
        ? h('button', { clase: 'tarjeta tarjeta--pulsable', tipo: 'button', onClick }, ...hijos)
        : h('div', { clase: 'tarjeta' }, ...hijos);
}
export function titulo(texto, sub) {
    const f = document.createDocumentFragment();
    f.append(h('h1', { clase: 'titulo', texto }));
    if (sub)
        f.append(h('p', { clase: 'subtitulo', texto: sub }));
    return f;
}
export function aviso(tono, texto, queHacer) {
    return h('p', { clase: tono === 'neutro' ? 'aviso aviso--neutro' : 'aviso' }, etiqueta(tono, tono === 'alerta' ? 'no se puede' : tono === 'aviso' ? 'revisar' : 'nota'), ' ' + texto, queHacer ? h('span', { clase: 'aviso__quehacer', texto: ' ' + queHacer }) : null);
}
export function vacio(tituloTexto, detalle) {
    return h('div', { clase: 'vacio' }, h('p', { clase: 'vacio__titulo', texto: tituloTexto }), h('p', { clase: 'vacio__detalle', texto: detalle }));
}
export function campo(etiquetaTexto, control) {
    return h('label', { clase: 'campo' }, h('span', { clase: 'campo__etiqueta', texto: etiquetaTexto }), control);
}
export function entradaNumero(valor, alCambiar) {
    const input = h('input', {
        clase: 'campo__control',
        modoEntrada: 'numeric',
        valor,
        // CNF: cada dato queda guardado de inmediato. No hay botón de guardar.
        onInput: (e) => {
            const el = e.target;
            el.value = el.value.replace(/[^\d]/g, '');
            alCambiar(el.value);
        },
    });
    return input;
}
export function selector(opciones, seleccionado, alCambiar) {
    const sel = h('select', {
        clase: 'campo__control',
        onChange: (e) => alCambiar(e.target.value),
    });
    for (const o of opciones) {
        const op = h('option', { valor: o.valor, texto: o.texto });
        if (o.valor === seleccionado)
            op.selected = true;
        sel.append(op);
    }
    return sel;
}
export function cifra(n, etiquetaTexto) {
    return h('div', { clase: 'cifra' }, h('span', { clase: 'cifra__n', texto: String(n) }), h('span', { clase: 'cifra__etiqueta', texto: etiquetaTexto }));
}
export function tabla(cabeceras, filas) {
    return h('div', { clase: 'tabla__caja' }, h('table', { clase: 'tabla' }, h('thead', {}, h('tr', {}, ...cabeceras.map((c) => h('th', { texto: c })))), h('tbody', {}, ...filas.map((f) => h('tr', {}, ...f.map((c) => h('td', {}, c)))))));
}
