package co.florlogic.finca.dispositivos.servicios.casosuso.impl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.dispositivos.dominio.AsignacionDelDia;
import co.florlogic.finca.dispositivos.dominio.Dispositivo;
import co.florlogic.finca.dispositivos.servicios.casosuso.AsignarCasoUso;
import co.florlogic.finca.dispositivos.servicios.output.dto.AsignacionDto;
import co.florlogic.finca.dispositivos.servicios.output.ensamblador.DispositivoEnsambladorSalida;
import co.florlogic.finca.dispositivos.servicios.output.ports.repositorios.AsignacionRepositorio;
import co.florlogic.finca.dispositivos.servicios.output.ports.repositorios.DispositivoRepositorio;
import co.florlogic.finca.dispositivos.servicios.output.ports.serviciosexternos.Auditoria;

/**
 * Asigna las celdas del día a un celular aprobado (CT-13) y lo deja en la bitácora (CT-11). Reasignar reemplaza: lo que
 * el celular tenía ese día se borra en la misma transacción.
 */
@Service
public class AsignarCasoUsoImpl implements AsignarCasoUso {

    private final DispositivoRepositorio dispositivos;
    private final AsignacionRepositorio asignaciones;
    private final Auditoria auditoria;
    private final Clock reloj;

    public AsignarCasoUsoImpl(DispositivoRepositorio dispositivos, AsignacionRepositorio asignaciones,
            Auditoria auditoria, Clock reloj) {
        this.dispositivos = dispositivos;
        this.asignaciones = asignaciones;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public AsignacionDelDia asignar(LocalDate jornada, UUID dispositivo, List<String> secciones, List<String> campos,
            String administrador) {
        Dispositivo celular = dispositivos.de(dispositivo).map(DispositivoEnsambladorSalida::aDominio)
                .orElseThrow(() -> new AsignacionDelDia.AsignacionInvalida("Celular desconocido"));
        AsignacionDelDia asignacion = AsignacionDelDia.para(jornada, celular, secciones, campos);
        Instant ahora = reloj.instant();
        asignaciones.reemplazar(jornada, dispositivo, asignacion.celdas().stream()
                .map(c -> new AsignacionDto(jornada, dispositivo, c.seccion(), c.campo(), ahora, administrador))
                .toList());
        auditoria.registrar(administrador, "asignacion_cambiada", dispositivo.toString(),
                Map.of("jornada", jornada.toString(), "celdas", Integer.toString(asignacion.celdas().size())));
        return asignacion;
    }

    @Override
    public List<AsignacionDelDia> de(LocalDate jornada) {
        Map<UUID, List<AsignacionDto>> porCelular = asignaciones.de(jornada).stream()
                .collect(Collectors.groupingBy(AsignacionDto::dispositivo, LinkedHashMap::new, Collectors.toList()));
        return porCelular.entrySet().stream()
                .map(e -> AsignacionDelDia.leida(jornada, e.getKey(), e.getValue().stream()
                        .map(a -> new AsignacionDelDia.Celda(a.seccion(), a.campo())).toList()))
                .toList();
    }
}
