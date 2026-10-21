package co.florlogic.simulador.servicios.puertos.entrada;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.lectura.LecturaResultado;

/**
 * Puerto de entrada: entregar una partición al nodo de finca (plantilla endpoint T4: un puerto por caso de uso,
 * nombrado por su intención). La orden de línea de comandos solo conoce esto.
 *
 * <p>Usa tipos del contrato porque el simulador es el otro lado del Published Language; la prohibición de usar
 * {@code contrato} es solo para {@code dominio} (A-44).
 */
public interface SincronizarParticionPuertoEntrada {

    /** Nunca lanza (A-57): todo fallo vuelve como {@link LecturaResultado.Fallida} con su clase. */
    LecturaResultado<ResultadoParticionDto> ejecutar(SincronizarParticionComando comando);
}
