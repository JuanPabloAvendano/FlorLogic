package co.florlogic.simulador.servicios.puertos.salida;

import java.net.URI;
import java.util.UUID;

import co.florlogic.contrato.dto.ResultadoParticion;
import co.florlogic.contrato.lectura.Lectura;

/** Puerto de salida: el nodo de finca visto desde el celular. Lo implementa el cliente HTTP. */
public interface NodoDeFinca {

    /** Entrega la partición y devuelve lo que respondió el nodo. Nunca lanza (A-57). */
    Lectura<ResultadoParticion> enviar(URI nodo, UUID sesion, byte[] particionJson);
}
