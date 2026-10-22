package co.florlogic.finca.ingesta.servicios.input.adapter;

import java.util.List;

import org.springframework.stereotype.Service;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticionCasoUso;
import co.florlogic.finca.ingesta.servicios.input.dto.ParticionRecibidaRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.RecibirParticionEntradaDto;
import co.florlogic.finca.ingesta.servicios.input.port.RecibirParticionPuertoEntrada;

/**
 * Ensamblador de entrada: implementa el puerto, arma el dominio desde el DTO, llama al caso de uso y arma la respuesta
 * desde lo que este devuelve. Es el Assembler de Fowler (PoEAA secc. 100, «Data Transfer Object»): así ni el DTO
 * depende del dominio ni el dominio del DTO. No decide nada: las reglas son del caso de uso y del dominio.
 */
@Service
public class RecibirParticionEnsambladorEntrada implements RecibirParticionPuertoEntrada {

    private final RecibirParticionCasoUso casoUso;

    public RecibirParticionEnsambladorEntrada(RecibirParticionCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public ParticionRecibidaRespuestaDto ejecutar(RecibirParticionEntradaDto entrada) {
        List<AnotacionRecibida> anotaciones = entrada.anotaciones().stream()
                .map(a -> new AnotacionRecibida(a.anotacionUuid(), a.consecutivo(), entrada.sesionId(), a.contenidoJson()))
                .toList();
        RecepcionDeParticionValor recepcion = casoUso.ejecutar(entrada.sesionId(), anotaciones);
        return new ParticionRecibidaRespuestaDto(
                recepcion.recibidas(), recepcion.aplicadas(), recepcion.sinEfecto(), recepcion.mayorConsecutivo());
    }
}
