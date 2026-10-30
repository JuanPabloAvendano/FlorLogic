/**
 * Métricas, logs, telemetría con esquema cerrado · TX-1 · TX-2 · Adaptadores de entrada ({@code …InputAdapter}): implementan el puerto de entrada y solo orquestan: piden el dominio al ensamblador, llaman al caso de uso y piden el response. No construyen dominio (A-84).
 */
package co.florlogic.finca.observabilidad.servicios.input.adapter;
