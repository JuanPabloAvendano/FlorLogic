package co.florlogic.finca.ingesta.servicios.casosuso;

import java.util.List;
import java.util.UUID;

import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;

/** Caso de uso: recibir una partición de una sesión abierta y juzgar cada anotación (ESPEC-dominio-ingesta §5). */
public interface RecibirParticionCasoUso {

    ResultadoDeRecepcion ejecutar(UUID sesionId, String persona, List<AnotacionRecibida> anotaciones);
}
