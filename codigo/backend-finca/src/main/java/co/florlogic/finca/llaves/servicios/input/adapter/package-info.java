/**
 * Custodia de llaves · TX-1 (firma) · TX-2 (cifra) · Adaptadores de entrada ({@code …InputAdapter}): implementan el puerto de entrada y solo orquestan: piden el dominio al ensamblador, llaman al caso de uso y piden el response. No construyen dominio (A-84).
 */
package co.florlogic.finca.llaves.servicios.input.adapter;
