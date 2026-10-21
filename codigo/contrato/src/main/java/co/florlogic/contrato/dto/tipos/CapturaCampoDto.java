package co.florlogic.contrato.dto.tipos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import tools.jackson.databind.JsonNode;

/**
 * Los {@code datos} del tipo {@code captura.campo}, versión 1 (A-20). En v1 solo se anota a nivel
 * de sección; otro nivel es una versión mayor (A-24). Viajan los datos de la clave del hecho,
 * pero la clave la calcula el ingreso (A-3).
 *
 * @param jornada el día natural de la finca en su zona horaria (P-S04, aceptado 19-oct-2026); la
 *     zona la fija la configuración, no el contrato
 * @param valor ausente o {@code null} es el dato ausente (A-33); aquí los dos llegan como {@code null}
 * @param motivoAusencia id del catálogo; solo cuando falta el valor
 * @param motivoDesviacion id del catálogo; cuando una regla pide motivo por una desviación brusca (A-49 ⟦4⟧)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CapturaCampoDto(
        @JsonProperty("produccion_id") String produccionId,
        @JsonProperty("seccion_id") String seccionId,
        @JsonProperty("campo_id") String campoId,
        @JsonProperty("jornada") LocalDate jornada,
        @JsonProperty("valor") JsonNode valor,
        @JsonProperty("motivo_ausencia") String motivoAusencia,
        @JsonProperty("motivo_desviacion") String motivoDesviacion) {

    /** Nombre del tipo en el sobre de la anotación. */
    public static final String TIPO = "captura.campo";

    public CapturaCampoDto {
        // Jackson entrega un null del JSON como NullNode; se iguala al ausente (A-33).
        if (valor != null && valor.isNull()) {
            valor = null;
        }
    }

    /** El dato como Null Object (A-34). Nunca lanza: valor y motivo a la vez es {@link ValorDto.Contradictorio} (A-57). */
    public ValorDto dato() {
        if (valor != null && motivoAusencia != null) {
            return new ValorDto.Contradictorio(valor, motivoAusencia);
        }
        if (valor != null) {
            return new ValorDto.Hay(valor);
        }
        return new ValorDto.Falta(motivoAusencia == null
                ? ValorDto.Motivo.SIN_MOTIVO
                : new ValorDto.Motivo.Declarado(motivoAusencia));
    }
}
