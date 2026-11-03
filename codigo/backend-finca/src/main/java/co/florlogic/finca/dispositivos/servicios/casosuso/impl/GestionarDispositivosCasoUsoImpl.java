package co.florlogic.finca.dispositivos.servicios.casosuso.impl;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.dispositivos.DispositivoPendiente;
import co.florlogic.finca.dispositivos.dominio.Dispositivo;
import co.florlogic.finca.dispositivos.servicios.casosuso.GestionarDispositivosCasoUso;
import co.florlogic.finca.dispositivos.servicios.output.ensamblador.DispositivoEnsambladorSalida;
import co.florlogic.finca.dispositivos.servicios.output.ports.repositorios.DispositivoRepositorio;
import co.florlogic.finca.dispositivos.servicios.output.ports.serviciosexternos.Auditoria;

/**
 * Reconoce, aprueba y bloquea celulares (CT-10). El primero que se ve queda pendiente y se publica
 * {@link DispositivoPendiente} (lo escucha {@code notificacion} para avisar al administrador). Aprobar y bloquear quedan en la bitácora (CT-11).
 */
@Service
public class GestionarDispositivosCasoUsoImpl implements GestionarDispositivosCasoUso {

    private final DispositivoRepositorio dispositivos;
    private final ApplicationEventPublisher eventos;
    private final Auditoria auditoria;
    private final Clock reloj;

    public GestionarDispositivosCasoUsoImpl(DispositivoRepositorio dispositivos, ApplicationEventPublisher eventos,
            Auditoria auditoria, Clock reloj) {
        this.dispositivos = dispositivos;
        this.eventos = eventos;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public Dispositivo reconocer(UUID id) {
        return dispositivos.de(id).map(DispositivoEnsambladorSalida::aDominio).orElseGet(() -> {
            Dispositivo nuevo = Dispositivo.visto(id, reloj.instant());
            dispositivos.guardar(DispositivoEnsambladorSalida.aDto(nuevo));
            eventos.publishEvent(new DispositivoPendiente(id));
            return nuevo;
        });
    }

    @Override
    @Transactional
    public void aprobar(UUID id, String administrador) {
        Dispositivo d = reconocer(id);
        d.aprobar(administrador, reloj.instant());
        dispositivos.guardar(DispositivoEnsambladorSalida.aDto(d));
        auditoria.registrar(administrador, "dispositivo_aprobado", id.toString(), Map.of());
    }

    @Override
    @Transactional
    public void bloquear(UUID id, String administrador) {
        Dispositivo d = reconocer(id);
        d.bloquear(administrador, reloj.instant());
        dispositivos.guardar(DispositivoEnsambladorSalida.aDto(d));
        auditoria.registrar(administrador, "dispositivo_bloqueado", id.toString(), Map.of());
    }

    @Override
    public List<Dispositivo> todos() {
        return dispositivos.todos().stream().map(DispositivoEnsambladorSalida::aDominio).toList();
    }
}
