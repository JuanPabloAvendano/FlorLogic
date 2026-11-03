package co.florlogic.finca.dispositivos.infraestructura.input.controladores;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.DispositivoRespuestaDto;
import co.florlogic.finca.dispositivos.servicios.input.dto.solicitud.AsignacionSolicitudDto;
import co.florlogic.finca.dispositivos.servicios.input.ports.AdministrarDispositivosInputPort;

/**
 * La web del administrador (F4): aprobar o bloquear celulares (CT-10) y la asignación del día (CT-13). Solo el rol
 * {@code administrador} llega aquí ({@code WebDelNodo}); el administrador es el {@code sub} de su sesión.
 *
 * <p>{@code @RestController} que devuelve {@link ModelAndView}: Spring pinta la plantilla igual que con
 * {@code @Controller}, y el controlador sigue viendo solo lo web de Spring ({@code CapasTest}). Con {@code HX-Request}
 * (htmx) responde solo el pedazo que cambió.
 */
@RestController
class DispositivosControlador {

    private final AdministrarDispositivosInputPort administrar;

    DispositivosControlador(AdministrarDispositivosInputPort administrar) {
        this.administrar = administrar;
    }

    @GetMapping("/web/dispositivos")
    ModelAndView dispositivos() {
        return new ModelAndView("web/dispositivos", Map.of("dispositivos", administrar.todos()));
    }

    @PostMapping("/web/dispositivos/{id}/aprobar")
    ModelAndView aprobar(@PathVariable UUID id, Principal administrador,
            @RequestHeader(value = "HX-Request", required = false) String htmx) {
        return fila(administrar.aprobar(id, administrador.getName()), htmx);
    }

    @PostMapping("/web/dispositivos/{id}/bloquear")
    ModelAndView bloquear(@PathVariable UUID id, Principal administrador,
            @RequestHeader(value = "HX-Request", required = false) String htmx) {
        return fila(administrar.bloquear(id, administrador.getName()), htmx);
    }

    private static ModelAndView fila(DispositivoRespuestaDto d, String htmx) {
        return htmx == null ? new ModelAndView("redirect:/web/dispositivos")
                : new ModelAndView("web/dispositivos :: fila", Map.of("d", d));
    }

    @GetMapping("/web/asignacion")
    ModelAndView asignacion(@RequestParam(required = false) LocalDate jornada) {
        return paginaDeAsignacion(jornada == null ? administrar.jornadaDeHoy() : jornada, null);
    }

    /** Secciones y campos llegan como texto: separados por comas, espacios o renglones. */
    @PostMapping("/web/asignacion")
    ModelAndView asignar(@RequestParam LocalDate jornada, @RequestParam UUID dispositivo,
            @RequestParam(defaultValue = "") String secciones, @RequestParam(defaultValue = "") String campos,
            Principal administrador) {
        try {
            administrar.asignar(new AsignacionSolicitudDto(jornada, dispositivo, ids(secciones), ids(campos),
                    administrador.getName()));
            return new ModelAndView("redirect:/web/asignacion?jornada=" + jornada);
        } catch (AdministrarDispositivosInputPort.AsignacionRechazada e) {
            ModelAndView pagina = paginaDeAsignacion(jornada, e.getMessage());
            pagina.setStatus(HttpStatus.UNPROCESSABLE_CONTENT);
            return pagina;
        }
    }

    private ModelAndView paginaDeAsignacion(LocalDate jornada, String error) {
        ModelAndView pagina = new ModelAndView("web/asignacion");
        pagina.addObject("jornada", jornada);
        pagina.addObject("asignaciones", administrar.asignacionDe(jornada));
        pagina.addObject("aprobados", administrar.todos().stream().filter(d -> "APROBADO".equals(d.estado())).toList());
        pagina.addObject("error", error);
        return pagina;
    }

    private static List<String> ids(String texto) {
        return Arrays.stream(texto.split("[,\\s]+")).filter(s -> !s.isBlank()).toList();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @GetMapping / @PostMapping("/web/…"): la ruta y el método HTTP que atiende cada método.
 * - @PathVariable UUID id: el tramo {id} de la ruta; Spring convierte el texto a UUID y responde 400 si no lo es.
 * - Principal administrador (java.security): el usuario autenticado que Spring Security pone en la petición;
 *   getName() es el nombre que queda como «cambiado por» y «asignado por».
 * - @RequestHeader(value = "HX-Request", required = false): sin la cabecera de htmx llega null (formulario normal).
 * - @RequestParam: lee un parámetro del formulario o de la dirección (?jornada=…). required = false deja que falte
 *   (llega null y se usa la jornada de hoy); defaultValue = "" hace que un campo de texto ausente llegue vacío.
 *   Spring convierte el texto a LocalDate o UUID y responde 400 si no puede.
 * - "redirect:/web/…": prefijo de Spring MVC; responde con una redirección y el navegador vuelve a pedir la página
 *   (patrón POST → redirección → GET).
 * - "web/dispositivos :: fila": selector de fragmento de Thymeleaf; pinta solo el bloque th:fragment="fila" de la
 *   plantilla, con el dispositivo en la variable d, y htmx lo pone en lugar de la fila vieja.
 * - ModelAndView.addObject(nombre, valor): agrega un dato que la plantilla web/asignacion lee por ese nombre.
 * - pagina.setStatus(HttpStatus.UNPROCESSABLE_CONTENT): responde 422 («se entendió el pedido pero no se puede
 *   cumplir») junto con la página pintada con el error; es el nombre que Spring 7 le da a ese código.
 * - administrar.todos().stream().filter(d → "APROBADO".equals(d.estado())).toList(): Stream; deja solo los aprobados
 *   para la lista de celulares a los que se puede asignar.
 * - texto.split(…) con la expresión regular de coma o espacio en blanco repetidos: corta lo escrito en el formulario
 *   por comas, espacios o saltos de renglón; Arrays.stream(…).filter(s → !s.isBlank()).toList() quita los pedazos
 *   vacíos.
 */
