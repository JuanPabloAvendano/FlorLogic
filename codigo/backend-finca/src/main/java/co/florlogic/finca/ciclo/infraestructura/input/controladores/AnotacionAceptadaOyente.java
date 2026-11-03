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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component (Spring): Spring crea una sola instancia al arrancar y le pasa el RegistrarHechoInputPort por el
 *   constructor. La clase no es public porque nadie fuera del paquete la usa; a Spring no le hace falta.
 * - Logger / LoggerFactory.getLogger (SLF4J): el registro del nodo. En LOG.info(…) y LOG.warn(…) cada {} se
 *   reemplaza por el argumento siguiente; info para el tipo que se ignora, warn para la anotación que no se pudo leer.
 * - JsonMapper.builder().build() (Jackson 3, paquetes tools.jackson): el lector de JSON. Se arma uno propio y
 *   estático en lugar de pedirlo a Spring; ya construido se puede compartir entre hilos.
 * - @ApplicationModuleListener (Spring Modulith): convierte al(…) en oyente del evento AnotacionAceptada. Junta tres
 *   cosas: corre solo después del commit de la transacción que publicó el evento (la de ingesta), en otro hilo, y
 *   dentro de una transacción nueva propia. Si ingesta revierte, el ciclo nunca se entera.
 * - Registro de publicaciones de Modulith (activado en application.yaml, tabla event_publication, con
 *   republish-outstanding-events-on-restart): un evento cuyo oyente termina con excepción queda pendiente y se vuelve
 *   a entregar al reiniciar. Por eso el catch traga los errores de lectura (reintentar no los arregla), mientras que
 *   un fallo de la base dentro de registrar.ejecutar(…) no se atrapa y el evento queda para otro intento.
 * - JSON.readTree(texto): lee todo el contenido como un árbol de JsonNode, sin clase Java de destino.
 * - path("datos"): baja a un hijo; si no existe devuelve un MissingNode («nodo ausente») en vez de null, así la
 *   cadena path("tiempo").path("crudo_ms") no revienta cuando falta un tramo.
 * - asLong(): el número del nodo; da 0 si el nodo falta. asString(): su texto (en Jackson 3 se llama así lo que en
 *   Jackson 2 era asText).
 * - valor.isMissingNode() || valor.isNull(): el campo no vino o vino como null de JSON → se guarda valor null (dato
 *   ausente). Si sí vino, valor.toString() lo guarda como texto JSON tal cual: 40 queda "40", y un texto queda con
 *   sus comillas.
 * - isString(): el motivo de ausencia solo se toma si es un texto JSON.
 * - LocalDate.parse(…): espera la fecha como aaaa-mm-dd; otra forma lanza DateTimeParseException.
 * - Instant.ofEpochMilli(crudoMs - evento.desfaseMs()): convierte milisegundos desde 1970 en un instante, después de
 *   restar el desfase del reloj del celular medido en su sesión: es la «hora del celular corregida».
 * - catch (JacksonException | DateTimeParseException | NullPointerException e): multi-catch, un mismo bloque para los
 *   tres tipos de error.
 */
