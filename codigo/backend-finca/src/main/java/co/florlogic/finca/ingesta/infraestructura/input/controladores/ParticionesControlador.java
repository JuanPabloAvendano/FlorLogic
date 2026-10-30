package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import java.net.URI;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.florlogic.contrato.dto.AnotacionDto;
import co.florlogic.contrato.dto.ConteoDto;
import co.florlogic.contrato.dto.ParticionDto;
import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.lectura.LectorDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.finca.ingesta.servicios.input.dto.response.RecibirParticionResponse;
import co.florlogic.finca.ingesta.servicios.input.dto.request.RecibirParticionRequest;
import co.florlogic.finca.ingesta.servicios.input.ports.RecibirParticionInputPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adaptador de entrada del camino mínimo de TX-1 (D-17; plantilla endpoint T6: un controlador por caso
 * de uso, que solo conoce el puerto y sus DTO de entrada; nunca el dominio).
 *
 * <p>Regla por defecto, decidida por Juan el 19-oct: <b>cualquier fallo de forma o de estructura es un
 * rechazo directo, sin motivo</b> ({@code 422 rechazado}). Los motivos finos ({@code sobre-invalido},
 * {@code pointer}, rechazo por anotación según A-58) llegan después.
 *
 * <p>Lee los bytes él mismo, en vez de dejar que Spring convierta el cuerpo: así un JSON roto también es
 * un 422 «rechazado» y no un 400 con otro formato, y la lectura pasa por la barrera del contrato, que
 * nunca lanza (A-57).
 */
@RestController
class ParticionesControlador {

    static final URI RECHAZADO = URI.create("https://florlogic.co/problemas/ingesta/rechazado");
    /** Lo inesperado (C-116): lo responde {@link ManejadorDeErrores}, sin detalles internos. */
    static final URI ERROR_INTERNO = URI.create("https://florlogic.co/problemas/ingesta/error-interno");

    // Solo para armar el árbol; convertirlo a DTO lo hace LectorDto.estricto() (A-9).
    private static final ObjectMapper JSON = JsonMapper.builder().build();

    private final RecibirParticionInputPort recibir;
    // recibido_ms es el reloj del nodo de finca (A-45 ⟦2⟧b).
    private final Clock reloj = Clock.systemUTC();

    ParticionesControlador(RecibirParticionInputPort recibir) {
        this.recibir = recibir;
    }

    @PostMapping(path = "/ingesta/v1/sesiones/{id}/particiones", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> recibir(@PathVariable("id") String id, @RequestBody(required = false) byte[] cuerpo) {
        UUID sesionId = uuidONull(id);
        JsonNode arbol = arbolONull(cuerpo);
        if (sesionId == null || arbol == null || !formaMinima(arbol) || !guardable(arbol.path("anotaciones"))) {
            return rechazado();
        }
        if (!(LectorDto.estricto().leer(arbol, ParticionDto.class) instanceof LecturaResultado.Leida<ParticionDto> leida)) {
            return rechazado();
        }
        ParticionDto peticion = leida.valor();

        List<RecibirParticionRequest.Anotacion> anotaciones = new ArrayList<>();
        JsonNode crudas = arbol.get("anotaciones");
        for (int i = 0; i < peticion.anotaciones().size(); i++) {
            AnotacionDto a = peticion.anotaciones().get(i);
            if (a.anotacionUuid() == null) {
                return rechazado();
            }
            // Se guarda el árbol de la anotación, no el DTO reescrito: así no se pierden campos que el DTO no conoce.
            // Es JSON equivalente, no los bytes recibidos (A-59).
            anotaciones.add(new RecibirParticionRequest.Anotacion(a.anotacionUuid(), a.consecutivo(), crudas.get(i).toString()));
        }

        RecibirParticionResponse resultado = recibir.ejecutar(new RecibirParticionRequest(sesionId, anotaciones));

        // Simplificaciones del camino mínimo, anotadas como «después» en la ESPEC §8: confirmado_hasta es el
        // mayor consecutivo de la partición (no la marca de agua real, A-31) y nada se marca ni se rechaza
        // por anotación todavía.
        ResultadoParticionDto respuesta = new ResultadoParticionDto(
                peticion.particionUuid(),
                resultado.mayorConsecutivo(),
                0,
                reloj.millis(),
                new ConteoDto(resultado.recibidas(), resultado.aplicadas(), 0, 0, resultado.sinEfecto()),
                List.of(),
                null,
                null);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(respuesta);
    }

    /**
     * Lo mínimo para que el canal funcione: la versión, la partición y el arreglo de anotaciones (A-32).
     *
     * <p>Complementa al lector estricto, no lo repite: el lector atrapa lo que <em>sobra o está mal escrito</em>
     * (un campo desconocido, texto en un número), pero el DTO tolera lo que <em>falta</em> (A-57): sin
     * {@code anotaciones} daría una lista vacía y un 200, y sin {@code particion_uuid} una respuesta sin
     * partición. Se usa {@code path} y no {@code get} porque un campo ausente da un nodo vacío (Null Object) y
     * no un {@code null}.
     */
    private static boolean formaMinima(JsonNode arbol) {
        return arbol.isObject()
                && arbol.path("contrato").isString()
                && uuidONull(arbol.path("particion_uuid").asString(null)) != null
                && arbol.path("anotaciones").isArray()
                && soloObjetos(arbol.path("anotaciones"));
    }

    /**
     * Cada anotación tiene que ser un objeto (OBS-2-1). El DTO quita los {@code null} de la lista (A-57) y el
     * árbol crudo no; si se dejara pasar {@code [null, A, B]}, el bucle de {@link #recibir} emparejaría la
     * anotación A con el JSON de otra y se guardaría contenido ajeno con un 200.
     */
    private static boolean soloObjetos(JsonNode arreglo) {
        for (JsonNode elemento : arreglo) {
            if (!elemento.isObject()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Lo que PostgreSQL no puede guardar se rechaza aquí, no en la base (COH-105, decisión de Juan del 20-oct). El
     * carácter nulo (U+0000, que en JSON se escribe con su escape) es JSON válido y el lector lo acepta, pero {@code jsonb} lo rechaza: sin este
     * filtro la partición daría un 500 en cada intento y no entraría nunca. Recorre nombres y textos de toda la
     * anotación; la profundidad ya la acota el parser de Jackson.
     */
    private static boolean guardable(JsonNode nodo) {
        if (nodo.isString()) {
            return nodo.asString().indexOf('\u0000') < 0;
        }
        for (String nombre : nodo.propertyNames()) {
            if (nombre.indexOf('\u0000') >= 0) {
                return false;
            }
        }
        for (JsonNode hijo : nodo.values()) {
            if (!guardable(hijo)) {
                return false;
            }
        }
        return true;
    }

    private static ResponseEntity<ProblemDetail> rechazado() {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, null);
        problema.setType(RECHAZADO);
        problema.setTitle("Partición rechazada");
        return ResponseEntity.unprocessableContent().cacheControl(CacheControl.noStore()).body(problema);
    }

    private static UUID uuidONull(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return UUID.fromString(texto);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static JsonNode arbolONull(byte[] cuerpo) {
        if (cuerpo == null || cuerpo.length == 0) {
            return null;
        }
        try {
            return JSON.readTree(cuerpo);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
