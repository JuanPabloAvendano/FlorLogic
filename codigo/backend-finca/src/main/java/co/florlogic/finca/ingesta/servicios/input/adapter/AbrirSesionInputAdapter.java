package co.florlogic.finca.ingesta.servicios.input.adapter;

import org.springframework.stereotype.Service;

import co.florlogic.finca.ingesta.servicios.casosuso.AbrirSesionCasoUso;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.AbrirSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.AbrirSesionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.ensamblador.SesionEnsambladorEntrada;
import co.florlogic.finca.ingesta.servicios.input.ports.AbrirSesionInputPort;

/** Adaptador de entrada de la apertura: solo orquesta ensamblador → caso de uso → ensamblador (A-84). */
@Service
public class AbrirSesionInputAdapter implements AbrirSesionInputPort {

    private final AbrirSesionCasoUso casoUso;

    public AbrirSesionInputAdapter(AbrirSesionCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public AbrirSesionResultadoDto ejecutar(AbrirSesionSolicitudDto solicitud) {
        return SesionEnsambladorEntrada.dispositivo(solicitud)
                .map(dispositivo -> casoUso.ejecutar(dispositivo, solicitud.persona(),
                        SesionEnsambladorEntrada.relojDelCelular(solicitud), solicitud.versionConfig()))
                .map(SesionEnsambladorEntrada::aResultado)
                .orElseGet(AbrirSesionResultadoDto.DispositivoInvalido::new);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service: Spring crea una instancia y la entrega al controlador que pide AbrirSesionInputPort.
 * - Optional .map(...).map(...).orElseGet(AbrirSesionResultadoDto.DispositivoInvalido::new): si el id del celular
 *   no sirve, el Optional queda vacío y se responde DispositivoInvalido; ::new es referencia al constructor.
 */
