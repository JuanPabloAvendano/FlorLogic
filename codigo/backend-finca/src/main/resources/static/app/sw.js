/* Service worker de la app de captura, servida por el nodo en /app/ (F8). Antes lo generaba construir.py; desde que
 * la app vive en el backend se mantiene a mano y PrecacheDeLaAppTest hace de construir.py: exige que ARCHIVOS tenga
 * todo archivo de la app (salvo sw.js y la página de limpieza) y que CACHE sea su huella, y dice el valor correcto
 * si no lo es. Cambiar el nombre del caché es lo que hace que los celulares bajen la versión nueva. */
const CACHE = 'florlogic-d98594343bfc';
const ARCHIVOS = ["./","estilos.css","icono-192.png","icono-512.png","index.html","js/almacen.js","js/auth.js","js/catalogo.js","js/escaner.js","js/id.js","js/main.js","js/metricas.js","js/modelo.js","js/nodo.js","js/particion.js","js/reglas.js","js/repositorio.js","js/sincronizacion.js","js/tipos.js","js/ui/bandeja.js","js/ui/captura.js","js/ui/catalogoPantallas.js","js/ui/comun.js","js/ui/datos.js","js/ui/medidas.js","js/vista.js","manifest.json","reglas.v1.json","seed.json"];

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
  // Solo lo propio y solo lecturas: Keycloak (/auth) y la ingesta son POST y van siempre a la red.
  if (e.request.method !== 'GET' || url.origin !== self.location.origin) return;

  // La página de limpieza nunca se intercepta: es la herramienta para reparar justamente esto.
  if (url.pathname.endsWith('/limpiar.html') || url.pathname.endsWith('/limpiar.js')) return;

  // Navegaciones: primero la red, y solo si no hay se cae al arranque guardado. Al revés
  // —caché primero— el service worker le contesta a CUALQUIER dirección con la misma página,
  // y entonces sigue sirviendo el sitio después de que su servidor ya no existe. También es lo
  // que hace llegar el regreso de Keycloak (/app/?code=…) a la página de verdad.
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
