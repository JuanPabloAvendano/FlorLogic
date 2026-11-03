package co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import co.florlogic.contrato.dto.AnotacionDto;
import co.florlogic.contrato.dto.ConteoDto;
import co.florlogic.contrato.dto.ExcepcionDto;
import co.florlogic.contrato.dto.ParticionDto;
import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.lectura.LectorDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Ensamblador del contrato: traduce, en los dos sentidos, entre el JSON del contrato y los DTO del puerto de entrada
 * (Martin · Clean Architecture p. 198: los adaptadores de interfaz convierten del formato de afuera al de los casos de
 * uso; A-103). Es la barrera del contrato: nunca lanza (A-57); si la partición no se puede leer, devuelve vacío y el
 * controlador responde {@code 422 rechazado}.
 *
 * <p>Regla por defecto, decidida por Juan el 19-oct: <b>cualquier fallo de forma o de estructura es un rechazo directo,
 * sin motivo</b>. Los motivos finos ({@code sobre-invalido}, {@code pointer}, rechazo por anotación según A-58) llegan
 * después. Sin estado, por eso son métodos estáticos y no un bean, como {@code RecibirParticionEnsambladorEntrada}.
 *
 * <p>Cada rechazo deja una línea de log con su motivo y sin contenido de la partición (OBS-R-12): el celular solo ve
 * {@code 422}, pero quien opera el nodo puede saber por qué.
 */
public final class ParticionEnsamblador {

    private static final Logger LOG = LoggerFactory.getLogger(ParticionEnsamblador.class);

    // Solo para armar el árbol; convertirlo a DTO lo hace LectorDto.estricto() (A-9).
    private static final ObjectMapper JSON = JsonMapper.builder().build();

    private ParticionEnsamblador() {
    }

    /**
     * Los bytes del cuerpo y el id de la ruta → la solicitud del puerto. Vacío si cualquier cosa no se puede leer: id
     * que no es UUID, JSON roto, forma mínima ausente, un carácter que PostgreSQL no guarda, un campo que el lector
     * estricto no admite, o una anotación sin {@code anotacion_uuid}.
     */
    public static Optional<ParticionLeidaDto> aSolicitud(String id, byte[] cuerpo, String persona) {
        UUID sesionId = uuidONull(id);
        if (sesionId == null) {
            return rechazo("la sesión de la ruta no es un UUID");
        }
        JsonNode arbol = arbolONull(cuerpo);
        if (arbol == null) {
            return rechazo("el cuerpo está vacío o no es JSON");
        }
        if (!formaMinima(arbol)) {
            return rechazo("falta la forma mínima (contrato, particion_uuid, anotaciones de objetos)");
        }
        if (!guardable(arbol.path("anotaciones"))) {
            return rechazo("trae U+0000, que jsonb no guarda");
        }
        if (!(LectorDto.estricto().leer(arbol, ParticionDto.class) instanceof LecturaResultado.Leida<ParticionDto> leida)) {
            return rechazo("el lector estricto del contrato no la admite");
        }
        ParticionDto particion = leida.valor();

        List<RecibirParticionSolicitudDto.Anotacion> anotaciones = new ArrayList<>();
        JsonNode crudas = arbol.get("anotaciones");
        for (int i = 0; i < particion.anotaciones().size(); i++) {
            AnotacionDto a = particion.anotaciones().get(i);
            if (a.anotacionUuid() == null) {
                return rechazo("una anotación no trae anotacion_uuid");
            }
            // Se guarda el árbol de la anotación, no el DTO reescrito: así no se pierden campos que el DTO no conoce.
            // Es JSON equivalente, no los bytes recibidos (A-59).
            anotaciones.add(new RecibirParticionSolicitudDto.Anotacion(a.anotacionUuid(), a.consecutivo(), a.tipo(),
                    a.versionTipo(), a.veredictoLocal() == AnotacionDto.VeredictoLocal.MARCADA, crudas.get(i).toString()));
        }
        return Optional.of(new ParticionLeidaDto(particion.particionUuid(), new RecibirParticionSolicitudDto(sesionId, persona, anotaciones)));
    }

    /**
     * La respuesta del puerto → el resultado del contrato.
     *
     * <p>{@code confirmado_hasta} es el avance real del celular (A-31); {@code excepciones[]} lleva las marcadas y las
     * rechazadas con sus códigos (A-23).
     *
     * @param recibidoMs el reloj del nodo de finca (A-45 ⟦2⟧b); lo pone el controlador
     */
    public static ResultadoParticionDto aRespuesta(UUID particionUuid, RecibirParticionRespuestaDto respuesta, long recibidoMs) {
        List<ExcepcionDto> excepciones = respuesta.excepciones().stream()
                .map(e -> new ExcepcionDto(e.anotacionUuid(), ExcepcionDto.Resultado.de(e.resultado()), e.codigos()))
                .toList();
        return new ResultadoParticionDto(
                particionUuid,
                respuesta.confirmadoHasta(),
                0,   // respaldado_hasta llega con TX-2
                recibidoMs,
                new ConteoDto(respuesta.recibidas(), respuesta.aplicadas(), respuesta.marcadas(), respuesta.rechazadas(),
                        respuesta.sinEfecto()),
                excepciones,
                null,
                null);
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
     * árbol crudo no; si se dejara pasar {@code [null, A, B]}, el bucle de {@link #aRequest} emparejaría la
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

    private static Optional<ParticionLeidaDto> rechazo(String motivo) {
        LOG.info("Partición rechazada (422): {}", motivo);
        return Optional.empty();
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
        // Solo lo que Jackson no puede leer es un rechazo; un error de programación sube y sale 500 (OBS-R-12).
        try {
            return JSON.readTree(cuerpo);
        } catch (JacksonException e) {
            return null;
        }
    }
}
