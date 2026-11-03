package co.florlogic.finca.ciclo.infraestructura.input.controladores;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import co.florlogic.finca.ciclo.servicios.input.ports.TrabajosDelGerenteInputPort;

/**
 * La cola del gerente en la web (F4, P-S01): las anotaciones marcadas que tiene que revisar. Solo el rol
 * {@code gerente} ({@code WebDelNodo}). Como en {@code DispositivosControlador}: {@code ModelAndView} y, con htmx, solo
 * el pedazo que cambió (la fila desaparece al resolverla).
 */
@RestController
class TrabajosControlador {

    private final TrabajosDelGerenteInputPort trabajos;

    TrabajosControlador(TrabajosDelGerenteInputPort trabajos) {
        this.trabajos = trabajos;
    }

    @GetMapping("/web/trabajos")
    ModelAndView trabajos() {
        return new ModelAndView("web/trabajos", Map.of("trabajos", trabajos.abiertos()));
    }

    @PostMapping("/web/trabajos/{anotacion}/resolver")
    ModelAndView resolver(@PathVariable UUID anotacion, Principal gerente,
            @RequestHeader(value = "HX-Request", required = false) String htmx) {
        boolean resuelto = trabajos.resolver(anotacion, gerente.getName());
        if (htmx == null) {
            return new ModelAndView("redirect:/web/trabajos");
        }
        // La fila se va en los dos casos, resuelta ahora o ya resuelta por otro gerente; solo cambia el aviso.
        return new ModelAndView("web/trabajos :: resuelta", Map.of("porOtro", !resuelto));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @RestController (Spring Web): cada método atiende una ruta HTTP y su resultado es la respuesta. Aquí devuelve
 *   ModelAndView, así que la respuesta es una página pintada con una plantilla Thymeleaf (lo explica el Javadoc).
 * - @GetMapping("/web/trabajos"): la cola del gerente al abrir la página.
 * - @PostMapping("/web/trabajos/{anotacion}/resolver"): el botón «resolver» de una fila. El tramo {anotacion} de la
 *   ruta llega en @PathVariable UUID anotacion; Spring convierte el texto a UUID y responde 400 si no lo es.
 * - Principal gerente (java.security): el usuario ya autenticado que Spring Security pone en la petición; getName()
 *   es su identificador y queda como quien resolvió el trabajo.
 * - @RequestHeader(value = "HX-Request", required = false): lee la cabecera que htmx añade a sus peticiones;
 *   required = false hace que, sin ella (formulario normal, sin JavaScript), llegue null en lugar de un error 400.
 * - new ModelAndView("web/trabajos", Map.of("trabajos", …)): la plantilla templates/web/trabajos.html y los datos que
 *   ve; Map.of arma un mapa inmutable.
 * - "redirect:/web/trabajos": prefijo de Spring MVC; en vez de pintar, responde con una redirección y el navegador
 *   vuelve a pedir la lista (patrón POST → redirección → GET, para que recargar no repita el POST).
 * - "web/trabajos :: resuelta": selector de fragmento de Thymeleaf; pinta solo el bloque th:fragment="resuelta" de
 *   esa plantilla, que htmx pone en lugar de la fila. porOtro decide qué aviso muestra.
 */
