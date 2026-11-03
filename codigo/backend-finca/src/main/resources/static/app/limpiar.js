/**
 * Página de limpieza de la PWA (limpiar.html). Vive fuera del precaché a propósito: tiene que cargar justo cuando el
 * service worker es el problema. Va en su propio archivo porque la CSP del nodo no deja correr scripts en línea.
 *
 * Borrar también lo capturado borra la identidad del celular: para el nodo es un celular nuevo, que vuelve a quedar
 * pendiente hasta que el administrador lo apruebe.
 */
const estado = document.getElementById('estado');
document.getElementById('origen').textContent = location.origin;

function linea(tono, texto) {
  const p = document.createElement('p');
  p.className = tono === 'neutro' ? 'aviso aviso--neutro' : 'aviso';
  p.textContent = texto;
  estado.append(p);
}

async function inventario() {
  estado.innerHTML = '';
  const sws = navigator.serviceWorker ? await navigator.serviceWorker.getRegistrations() : [];
  const caches_ = window.caches ? await caches.keys() : [];
  linea('neutro', sws.length + ' service worker(s) registrados en ' + location.origin);
  for (const r of sws) linea('neutro', '   · ' + (r.active && r.active.scriptURL || r.scope));
  linea('neutro', caches_.length + ' caché(s): ' + (caches_.join(', ') || 'ninguno'));
}

async function limpiar(tambienDatos) {
  estado.innerHTML = '';
  let n = 0;
  if (navigator.serviceWorker) {
    for (const r of await navigator.serviceWorker.getRegistrations()) { await r.unregister(); n++; }
  }
  let c = 0;
  if (window.caches) {
    for (const k of await caches.keys()) { await caches.delete(k); c++; }
  }
  linea('ok', 'Listo: ' + n + ' service worker(s) y ' + c + ' caché(s) borrados.');

  if (tambienDatos && window.indexedDB) {
    await new Promise((r) => {
      const s = indexedDB.deleteDatabase('florlogic-captura');
      s.onsuccess = s.onerror = s.onblocked = () => r();
    });
    try {
      for (const clave of Object.keys(localStorage)) {
        if (clave.startsWith('florlogic.')) localStorage.removeItem(clave);
      }
      sessionStorage.clear();
    } catch (e) { /* sin almacenamiento */ }
    linea('ok', 'También se borró lo capturado, la sesión y la identidad de este celular.');
  }
  linea('neutro', 'Cerrá TODAS las pestañas de este sitio y volvé a abrir ' + location.origin + '/app/');
}

document.getElementById('limpiar').addEventListener('click', () => limpiar(false));
document.getElementById('todo').addEventListener('click', () => limpiar(true));
inventario();
