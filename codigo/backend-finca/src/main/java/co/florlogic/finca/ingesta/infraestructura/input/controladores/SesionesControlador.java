package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador.SesionEnsamblador;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.AbrirSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.CerrarSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.AbrirSesionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.CerrarSesionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.ports.AbrirSesionInputPort;
import co.florlogic.finca.ingesta.servicios.input.ports.CerrarSesionInputPort;

/**
 * Adaptador de entrada de la sesión de sincronización (CT-1): abrir y cerrar. Solo habla HTTP; el JSON del contrato lo
 * traduce {@link SesionEnsamblador}.
 */
@RestController
class SesionesControlador {

    private final AbrirSesionInputPort abrir;
    private final CerrarSesionInputPort cerrar;

    SesionesControlador(AbrirSesionInputPort abrir, CerrarSesionInputPort cerrar) {
        this.abrir = abrir;
        this.cerrar = cerrar;
    }

    @PostMapping(path = "/ingesta/v1/sesiones", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> abrir(@RequestBody(required = false) byte[] cuerpo, Principal quien) {
        Optional<AbrirSesionSolicitudDto> solicitud = SesionEnsamblador.aAbrir(cuerpo, Personas.de(quien));
        if (solicitud.isEmpty()) {
            return Problemas.rechazado();
        }
        return switch (abrir.ejecutar(solicitud.get())) {
            case AbrirSesionResultadoDto.Abierta a -> ResponseEntity.status(HttpStatus.CREATED)
                    .cacheControl(CacheControl.noStore()).body(SesionEnsamblador.aAbierta(a.respuesta()));
            case AbrirSesionResultadoDto.DispositivoInvalido i -> Problemas.rechazado();
        };
    }

    @PostMapping(path = "/ingesta/v1/sesiones/{id}/cierre", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> cerrar(@PathVariable("id") String id, @RequestBody(required = false) byte[] cuerpo,
            Principal quien) {
        Optional<CerrarSesionSolicitudDto> solicitud = SesionEnsamblador.aCerrar(id, cuerpo, Personas.de(quien));
        if (solicitud.isEmpty()) {
            return Problemas.rechazado();
        }
        return switch (cerrar.ejecutar(solicitud.get())) {
            case CerrarSesionResultadoDto.Cerrada c -> ResponseEntity.ok().cacheControl(CacheControl.noStore())
                    .body(SesionEnsamblador.aCerrada(c.respuesta()));
            case CerrarSesionResultadoDto.SesionDesconocida s -> Problemas.sesionDesconocida();
            case CerrarSesionResultadoDto.SesionAjena s -> Problemas.sesionAjena();
        };
    }
}
