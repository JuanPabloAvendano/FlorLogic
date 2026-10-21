/**
 * Los mensajes del contrato como records (TEC-11): la ida ({@link co.florlogic.contrato.dto.ParticionDto})
 * y la vuelta ({@link co.florlogic.contrato.dto.ResultadoParticionDto}).
 *
 * <p>Cada campo lleva su {@code @JsonProperty("snake_case")} escrito a mano, y no un
 * {@code @JsonNaming} para todo el record (D3). Así el nombre del contrato se lee tal cual en el código,
 * hay nombres que una regla automática no daría ({@code desfaseDeclarado} ↔ {@code desfase_declarado_ms}),
 * y la forma no depende de cómo esté configurado cada lector: el estricto y el tolerante usan mappers
 * distintos (A-9).
 *
 * <p>Ningún constructor lanza (A-57). Estas clases no se leen directo desde la red: el nodo de finca valida
 * y lee por piezas (A-58).
 */
package co.florlogic.contrato.dto;
