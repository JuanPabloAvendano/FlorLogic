/* Generado por construir.py — no editar a mano. */
const CACHE = 'florlogic-acb3e7f7b184';
const ARCHIVOS = ["./", "estilos.css", "icono-192.png", "icono-512.png", "index.html", "js/almacen.js", "js/catalogo.js", "js/escaner.js", "js/id.js", "js/main.js", "js/metricas.js", "js/modelo.js", "js/reglas.js", "js/repositorio.js", "js/sincronizacion.js", "js/tipos.js", "js/ui/bandeja.js", "js/ui/captura.js", "js/ui/catalogoPantallas.js", "js/ui/comun.js", "js/ui/datos.js", "js/ui/medidas.js", "js/vista.js", "manifest.webmanifest", "reglas.v1.json", "seed.json"];

// El catálogo y las reglas entran al precaché a propósito: sin ellos la aplicación no arranca
// en el campo, y CN-13 dice que tiene que arrancar sin conexión.
self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE).then((c) => c.addAll(ARCHIVOS)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys()
      .then((claves) => Promise.all(claves.filter((k) => k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim()),
  );
});

self.addEventListener('fetch', (e) => {
  const url = new URL(e.request.url);
  if (e.request.method !== 'GET' || url.origin !== self.location.origin) return;

  // limpiar.html nunca se intercepta: es la herramienta para reparar justamente esto.
  if (url.pathname.endsWith('/limpiar.html')) return;

  // Navegaciones: primero la red, y solo si no hay se cae al arranque guardado. Al revés
  // —caché primero— el service worker le contesta a CUALQUIER dirección con la misma página,
  // y entonces sigue sirviendo el sitio después de que su servidor ya no existe.
  if (e.request.mode === 'navigate') {
    e.respondWith(fetch(e.request).catch(() => caches.match('./').then((r) => r || Response.error())));
    return;
  }
  e.respondWith(
    caches.match(e.request).then((r) => r || fetch(e.request).then((res) => {
      const copia = res.clone();
      caches.open(CACHE).then((c) => c.put(e.request, copia));
      return res;
    })),
  );
});
