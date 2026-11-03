package co.florlogic.finca.ingesta.servicios.casosuso;

import java.util.List;
import java.util.UUID;

import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;

/**
 * Caso de uso: recibir las anotaciones de una partición. Habla en dominio porque vive detrás del puerto: el servicio
 * de aplicación es el cliente directo del modelo (Vernon · Implementing Domain-Driven Design p. 170). Lo llama el
 * adaptador de entrada; lo implementa {@code impl}. Solo recibe en una sesión abierta de quien la abrió (CT-1).
 */
public interface RecibirParticionCasoUso {

    ResultadoDeRecepcion ejecutar(UUID sesionId, String persona, List<AnotacionRecibida> anotaciones);
}
