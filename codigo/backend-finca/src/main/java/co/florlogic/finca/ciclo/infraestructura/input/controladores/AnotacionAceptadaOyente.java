package co.florlogic.finca.ciclo.infraestructura.input.controladores;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import co.florlogic.finca.ciclo.servicios.input.dto.solicitud.AnotacionAceptadaSolicitudDto;
import co.florlogic.finca.ciclo.servicios.input.ports.RegistrarHechoInputPort;
import co.florlogic.finca.ingesta.AnotacionAceptada;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Adaptador de entrada por evento (CT-12): escucha {@link AnotacionAceptada} de ingesta después del commit, en su
 * propia transacción (Spring Modulith), lee los campos del hecho del JSON y llama al puerto. Solo entiende
 * {@code captura.campo}; otro tipo no es un hecho de producción y se ignora con un registro.
 */
@Component
class AnotacionAceptadaOyente {

    private static final Logger LOG = LoggerFactory.getLogger(AnotacionAceptadaOyente.class);
    private static final ObjectMapper JSON = JsonMapper.builder().build();

    private final RegistrarHechoInputPort registrar;

    AnotacionAceptadaOyente(RegistrarHechoInputPort registrar) {
        this.registrar = registrar;
    }

    @ApplicationModuleListener
    void al(AnotacionAceptada evento) {
        if (!"captura.campo".equals(evento.tipo())) {
            LOG.info("Ciclo: tipo {} sin hecho de producción; se ignora", evento.tipo());
            return;
        }
        try {
            JsonNode anotacion = JSON.readTree(evento.contenidoJson());
            JsonNode datos = anotacion.path("datos");
            long crudoMs = anotacion.path("tiempo").path("crudo_ms").asLong();
            JsonNode valor = datos.path("valor");
            registrar.ejecutar(new AnotacionAceptadaSolicitudDto(evento.anotacionUuid(), evento.dispositivoId(),
                    datos.path("produccion_id").asString(), datos.path("seccion_id").asString(),
                    datos.path("campo_id").asString(), LocalDate.parse(datos.path("jornada").asString()),
                    valor.isMissingNode() || valor.isNull() ? null : valor.toString(),
                    datos.path("motivo_ausencia").isString() ? datos.path("motivo_ausencia").asString() : null,
                    Instant.ofEpochMilli(crudoMs - evento.desfaseMs()), evento.codigos()));
        } catch (JacksonException | DateTimeParseException | NullPointerException e) {
            // Ya pasó las reglas, así que esto no debería ocurrir; si ocurre, queda en el log y el evento no se repite.
            LOG.warn("Ciclo: la anotación {} no tiene los campos de un hecho: {}", evento.anotacionUuid(), e.getMessage());
        }
    }
}
