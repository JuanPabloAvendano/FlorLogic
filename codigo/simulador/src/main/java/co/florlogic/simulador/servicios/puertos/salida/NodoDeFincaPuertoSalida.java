package co.florlogic.simulador.servicios.puertos.salida;

import java.net.URI;
import java.util.UUID;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.dto.SesionAbiertaDto;
import co.florlogic.contrato.dto.SesionCerradaDto;
import co.florlogic.contrato.lectura.LecturaResultado;

/** Puerto de salida: el nodo de finca visto desde el celular. Lo implementa el cliente HTTP. */
public interface NodoDeFincaPuertoSalida {

    /** Abre una sesión de sincronización (CT-1). Nunca lanza (A-57). */
    LecturaResultado<SesionAbiertaDto> abrir(URI nodo, UUID dispositivo, long relojMs, int versionConfig);

    /** Cierra la sesión, completa o parcial (CT-1). Nunca lanza. */
    LecturaResultado<SesionCerradaDto> cerrar(URI nodo, UUID sesion, boolean completa);

    /** Entrega la partición y devuelve lo que respondió el nodo. Nunca lanza (A-57). */
    LecturaResultado<ResultadoParticionDto> enviar(URI nodo, UUID sesion, byte[] particionJson);
}
