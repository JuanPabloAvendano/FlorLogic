package co.florlogic.finca.ingesta.servicios.casosuso;

import java.util.List;
import java.util.UUID;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.RecepcionDeParticionValor;

/**
 * Caso de uso: recibir las anotaciones de una partición. Habla en dominio porque vive detrás del puerto: el servicio
 * de aplicación es el cliente directo del modelo (Vernon · Implementing Domain-Driven Design p. 170). Lo llama el
 * ensamblador de entrada; lo implementa {@code impl}.
 */
public interface RecibirParticionCasoUso {

    RecepcionDeParticionValor ejecutar(UUID sesionId, List<AnotacionRecibida> anotaciones);
}
