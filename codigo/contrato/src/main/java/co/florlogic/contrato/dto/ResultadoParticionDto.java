package co.florlogic.contrato.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import co.florlogic.contrato.lectura.IdDeCatalogoInterfaz;

/**
 * Respuesta {@code 200} a una partición (A-13, A-26). Es la foto del momento: los conteos no se
 * reescriben después, y un reenvío se procesa de nuevo en vez de devolver una respuesta guardada.
 *
 * <p>El constructor no lanza (A-57): una respuesta que rompe el contrato se puede leer igual, y
 * {@link #incoherencias()} dice qué rompe. El nodo de finca no envía una respuesta con incoherencias;
 * el celular que recibe una la trata como anomalía: no borra nada por ella y sigue con lo demás.
 *
 * @param confirmadoHasta mayor consecutivo recibido sin huecos (A-31)
 * @param respaldadoHasta mayor consecutivo dentro de un respaldo verificado; lo calcula TX-2 (A-31)
 * @param recibidoMs reloj del nodo de finca; el celular lo devuelve en {@code primeros_acuses} (A-45 ⟦2⟧b)
 * @param excepciones solo las anotaciones con algo que mostrar; las aplicadas sin códigos no salen (A-23)
 * @param descartesAcusados todo aviso de descarte vuelve aquí (A-47); se omite si no hubo
 * @param accion solo si hace falta, p. ej. tras un consecutivo reiniciado (A-39)
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ResultadoParticionDto(
        @JsonProperty("particion_uuid") UUID particionUuid,
        @JsonProperty("confirmado_hasta") long confirmadoHasta,
        @JsonProperty("respaldado_hasta") long respaldadoHasta,
        @JsonProperty("recibido_ms") long recibidoMs,
        @JsonProperty("conteo") ConteoDto conteo,
        @JsonProperty("excepciones") @JsonInclude(JsonInclude.Include.ALWAYS) List<ExcepcionDto> excepciones,
        @JsonProperty("descartes_acusados") List<Long> descartesAcusados,
        @JsonProperty("accion") Accion accion) {

    public ResultadoParticionDto {
        excepciones = ListasUtilidad.copia(excepciones);
        descartesAcusados = ListasUtilidad.copia(descartesAcusados);
    }

    /** Las reglas del contrato que esta respuesta rompe; vacía si está bien (Notification, A-57). */
    @JsonIgnore
    public List<String> incoherencias() {
        List<String> encontradas = new ArrayList<>();
        if (particionUuid == null) {
            encontradas.add("particion_uuid_ausente");
        }
        if (confirmadoHasta < 0 || respaldadoHasta < 0 || recibidoMs < 0) {
            encontradas.add("marca_negativa");
        }
        if (respaldadoHasta > confirmadoHasta) {
            encontradas.add("respaldado_mayor_que_confirmado");
        }
        if (conteo == null) {
            encontradas.add("conteo_ausente");
        } else {
            encontradas.addAll(conteo.incoherencias());
        }
        for (ExcepcionDto excepcion : excepciones) {
            if (excepcion.anotacionUuid() == null) {
                encontradas.add("excepcion_sin_anotacion_uuid");
            }
            if (excepcion.resultado() == null || excepcion.resultado() == ExcepcionDto.Resultado.DESCONOCIDO) {
                encontradas.add("resultado_desconocido");
            }
        }
        if (accion == Accion.DESCONOCIDA) {
            encontradas.add("accion_desconocida");
        }
        return List.copyOf(encontradas);
    }

    /** {@code DESCONOCIDA}: un nodo de finca más nuevo pide algo que este celular no sabe hacer (A-57). */
    public enum Accion implements IdDeCatalogoInterfaz {
        /** El dispositivo reinició sus consecutivos y tiene que registrarse otra vez (A-39). */
        REGISTRAR_DE_NUEVO("registrar_de_nuevo"),
        DESCONOCIDA(null);

        private final String id;

        Accion(String id) {
            this.id = id;
        }

        @JsonValue
        @Override
        public String id() {
            return id;
        }

        @JsonCreator
        public static Accion de(String id) {
            return IdDeCatalogoInterfaz.buscar(values(), id, DESCONOCIDA);
        }
    }
}
