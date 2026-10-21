package co.florlogic.simulador.servicios.puertos.entrada;

import co.florlogic.contrato.dto.ResultadoParticion;
import co.florlogic.contrato.lectura.Lectura;

/**
 * Puerto de entrada: entregar una partición al nodo de finca (plantilla endpoint T4: un puerto por caso de uso,
 * nombrado por su intención). La orden de línea de comandos solo conoce esto.
 *
 * <p>Usa tipos del contrato porque el simulador es el otro lado del Published Language; la prohibición de usar
 * {@code contrato} es solo para {@code dominio} (A-44).
 */
public interface SincronizarParticion {

    /** Nunca lanza (A-57): todo fallo vuelve como {@link Lectura.Fallida} con su clase. */
    Lectura<ResultadoParticion> ejecutar(SincronizarParticionComando comando);
}
