package co.florlogic.finca.ingesta.infraestructura.entrada;

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

import co.florlogic.contrato.dto.Anotacion;
import co.florlogic.contrato.dto.Conteo;
import co.florlogic.contrato.dto.ParticionRequest;
import co.florlogic.contrato.dto.ResultadoParticion;
import co.florlogic.contrato.lectura.LectorDto;
import co.florlogic.contrato.lectura.Lectura;
import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.servicios.casosuso.ParticionRecibida;
import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticion;
import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticionComando;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adaptador de entrada del camino mínimo de TX-1 (D-17; plantilla endpoint T6: un controlador por caso
 * de uso, que solo conoce el puerto).
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
class ControladorParticiones {

    static final URI RECHAZADO = URI.create("https://florlogic.co/problemas/ingesta/rechazado");

    // Solo para armar el árbol; convertirlo a DTO lo hace LectorDto.estricto() (A-9).
    private static final ObjectMapper JSON = JsonMapper.builder().build();

    private final RecibirParticion recibir;
    // recibido_ms es el reloj del nodo de finca (A-45 ⟦2⟧b).
    private final Clock reloj = Clock.systemUTC();

    ControladorParticiones(RecibirParticion recibir) {
        this.recibir = recibir;
    }

    @PostMapping(path = "/ingesta/v1/sesiones/{id}/particiones", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> recibir(@PathVariable("id") String id, @RequestBody(required = false) byte[] cuerpo) {
        UUID sesionId = uuidONull(id);
        JsonNode arbol = arbolONull(cuerpo);
        if (sesionId == null || arbol == null || !formaMinima(arbol)) {
            return rechazado();
        }
        if (!(LectorDto.estricto().leer(arbol, ParticionRequest.class) instanceof Lectura.Leida<ParticionRequest> leida)) {
            return rechazado();
        }
        ParticionRequest peticion = leida.valor();

        List<AnotacionRecibida> anotaciones = new ArrayList<>();
        JsonNode crudas = arbol.get("anotaciones");
        for (int i = 0; i < peticion.anotaciones().size(); i++) {
            Anotacion a = peticion.anotaciones().get(i);
            if (a.anotacionUuid() == null) {
                return rechazado();
            }
            // Se guarda el JSON de la anotación tal como llegó, no el DTO reescrito (A-33).
            anotaciones.add(new AnotacionRecibida(a.anotacionUuid(), a.consecutivo(), sesionId, crudas.get(i).toString()));
        }

        ParticionRecibida resultado = recibir.ejecutar(new RecibirParticionComando(sesionId, anotaciones));

        // Simplificaciones del camino mínimo, anotadas como «después» en la ESPEC §8: confirmado_hasta es el
        // mayor consecutivo de la partición (no la marca de agua real, A-31) y nada se marca ni se rechaza
        // por anotación todavía.
        ResultadoParticion respuesta = new ResultadoParticion(
                peticion.particionUuid(),
                resultado.mayorConsecutivo(),
                0,
                reloj.millis(),
                new Conteo(resultado.recibidas(), resultado.aplicadas(), 0, 0, resultado.sinEfecto()),
                List.of(),
                null,
                null);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(respuesta);
    }

    /** Lo mínimo para que el canal funcione: la versión, la partición y el arreglo de anotaciones (A-32). */
    private static boolean formaMinima(JsonNode arbol) {
        return arbol.isObject()
                && arbol.path("contrato").isString()
                && uuidONull(arbol.path("particion_uuid").asString(null)) != null
                && arbol.path("anotaciones").isArray();
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
