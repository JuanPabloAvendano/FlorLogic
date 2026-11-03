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
