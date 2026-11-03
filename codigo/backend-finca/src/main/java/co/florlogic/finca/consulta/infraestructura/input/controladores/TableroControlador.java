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
