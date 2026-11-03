package co.florlogic.contrato.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta {@code 200} del cierre (CT-1; recorrido de TX-1, paso 15).
 *
 * @param estado {@code cerrada} o {@code parcial}
 * @param versionConfig la versión de configuración vigente, para que el celular la descargue si es más nueva
 * @param avisos textos cortos para quien entrega, sin datos de negocio
 */
public record SesionCerradaDto(
        @JsonProperty("sesion_uuid") UUID sesionUuid,
        @JsonProperty("estado") String estado,
        @JsonProperty("confirmado_hasta") long confirmadoHasta,
        @JsonProperty("version_config") int versionConfig,
        @JsonProperty("avisos") List<String> avisos) {

    public SesionCerradaDto {
        avisos = ListasUtilidad.copia(avisos);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con @JsonProperty: cada miembro con su nombre del contrato en snake_case.
 * - public SesionCerradaDto { avisos = … }: constructor compacto del record; cambia la lista recibida por una
 *   copia inmutable sin null antes de guardarla (ausente queda como lista vacía).
 */
