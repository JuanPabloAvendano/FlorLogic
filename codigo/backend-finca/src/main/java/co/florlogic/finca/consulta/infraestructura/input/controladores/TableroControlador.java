package co.florlogic.finca.consulta.infraestructura.input.controladores;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import co.florlogic.finca.consulta.servicios.input.ports.TableroInputPort;

/**
 * El tablero de la web (F4, CT-13): por celular y por sección, esperado, recibido y falta. La página se refresca sola
 * con htmx ({@code /web/tablero/filas} cada 30 s, sin recargar). Gerente y administrador ({@code WebDelNodo}).
 * Como en {@code DispositivosControlador}: {@code @RestController} con {@code ModelAndView}.
 */
@RestController
class TableroControlador {

    private final TableroInputPort tablero;

    TableroControlador(TableroInputPort tablero) {
        this.tablero = tablero;
    }

    @GetMapping({"/web", "/web/"})
    ModelAndView inicio() {
        return new ModelAndView("redirect:/web/tablero");
    }

    @GetMapping("/web/tablero")
    ModelAndView pagina(@RequestParam(required = false) LocalDate jornada) {
        return new ModelAndView("web/tablero", Map.of("t", tablero.de(jornada == null ? tablero.hoy() : jornada)));
    }

    @GetMapping("/web/tablero/filas")
    ModelAndView filas(@RequestParam LocalDate jornada) {
        return new ModelAndView("web/tablero :: filas", Map.of("t", tablero.de(jornada)));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @RestController con ModelAndView: normalmente @RestController devuelve datos; al devolver un ModelAndView, Spring
 *   pinta la plantilla Thymeleaf que nombra (templates/web/tablero.html) con los datos del mapa.
 * - @GetMapping({"/web", "/web/"}): atiende peticiones GET en las dos rutas, con y sin barra final.
 * - "redirect:/web/tablero": prefijo especial del nombre de vista; Spring responde una redirección HTTP al tablero en
 *   vez de pintar una plantilla.
 * - @RequestParam(required = false) LocalDate jornada: toma ?jornada= de la URL y Spring lo convierte a LocalDate con
 *   el formato año-mes-día que fija spring.mvc.format.date: iso en application.yaml (por eso no hace falta
 *   @DateTimeFormat aquí); si no viene llega null y se usa hoy (la jornada en la zona de la finca).
 * - @RequestParam LocalDate jornada (en filas): sin required = false es obligatorio; si falta, Spring responde 400.
 *   La plantilla siempre lo manda en el hx-get de la sección filas.
 * - Map.of("t", ...): el modelo; dentro de la plantilla el tablero se lee como la variable t.
 * - "web/tablero :: filas": sintaxis de fragmento de Thymeleaf; pinta solo el bloque marcado th:fragment="filas" de
 *   esa plantilla. Es lo que htmx pide cada 30 s (hx-trigger="every 30s") para cambiar solo la tabla sin recargar.
 * - Clase y constructor sin public: Spring igual la crea; nadie más la nombra.
 */
