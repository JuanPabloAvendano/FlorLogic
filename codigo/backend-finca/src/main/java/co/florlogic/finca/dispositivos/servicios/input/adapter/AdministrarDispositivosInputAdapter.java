package co.florlogic.finca.dispositivos.servicios.input.adapter;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.florlogic.finca.dispositivos.dominio.AsignacionDelDia;
import co.florlogic.finca.dispositivos.servicios.casosuso.AsignarCasoUso;
import co.florlogic.finca.dispositivos.servicios.casosuso.GestionarDispositivosCasoUso;
import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.AsignacionRespuestaDto;
import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.DispositivoRespuestaDto;
import co.florlogic.finca.dispositivos.servicios.input.dto.solicitud.AsignacionSolicitudDto;
import co.florlogic.finca.dispositivos.servicios.input.ensamblador.AdministrarDispositivosEnsambladorSalida;
import co.florlogic.finca.dispositivos.servicios.input.ports.AdministrarDispositivosInputPort;

/** Adaptador de entrada de la web del administrador: orquesta caso de uso → ensamblador (A-84). */
@Service
public class AdministrarDispositivosInputAdapter implements AdministrarDispositivosInputPort {

    private final GestionarDispositivosCasoUso gestionar;
    private final AsignarCasoUso asignar;
    private final Clock relojDeLaFinca;

    public AdministrarDispositivosInputAdapter(GestionarDispositivosCasoUso gestionar, AsignarCasoUso asignar,
            Clock reloj, ZoneId zonaDeLaFinca) {
        this.gestionar = gestionar;
        this.asignar = asignar;
        this.relojDeLaFinca = reloj.withZone(zonaDeLaFinca);
    }

    @Override
    public LocalDate jornadaDeHoy() {
        return LocalDate.now(relojDeLaFinca);
    }

    @Override
    public List<DispositivoRespuestaDto> todos() {
        return gestionar.todos().stream().map(AdministrarDispositivosEnsambladorSalida::aDto).toList();
    }

    @Override
    public DispositivoRespuestaDto aprobar(UUID dispositivo, String administrador) {
        gestionar.aprobar(dispositivo, administrador);
        return AdministrarDispositivosEnsambladorSalida.aDto(gestionar.reconocer(dispositivo));
    }

    @Override
    public DispositivoRespuestaDto bloquear(UUID dispositivo, String administrador) {
        gestionar.bloquear(dispositivo, administrador);
        return AdministrarDispositivosEnsambladorSalida.aDto(gestionar.reconocer(dispositivo));
    }

    @Override
    public List<AsignacionRespuestaDto> asignacionDe(LocalDate jornada) {
        return asignar.de(jornada).stream().map(AdministrarDispositivosEnsambladorSalida::aDto).toList();
    }

    @Override
    public AsignacionRespuestaDto asignar(AsignacionSolicitudDto s) {
        try {
            return AdministrarDispositivosEnsambladorSalida.aDto(
                    asignar.asignar(s.jornada(), s.dispositivo(), s.secciones(), s.campos(), s.administrador()));
        } catch (AsignacionDelDia.AsignacionInvalida e) {
            throw new AsignacionRechazada(e.getMessage());
        }
    }
}
