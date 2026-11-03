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
 * {@link DispositivoPendiente} (para que {@code notificacion} avise al administrador; ese oyente llega en F5). Aprobar y bloquear quedan en la bitácora (CT-11).
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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): Spring crea la instancia y la entrega a quien pida GestionarDispositivosCasoUso.
 * - ApplicationEventPublisher (Spring): el publicador de eventos de la aplicación. eventos.publishEvent(new
 *   DispositivoPendiente(id)) avisa que llegó un celular desconocido. Hoy ningún oyente lo recibe: el de notificacion
 *   llega en la fase F5; mientras tanto el evento se publica y nadie lo usa.
 * - @Transactional: cada operación es una transacción; aprobar y bloquear llaman a reconocer(…) desde la misma clase,
 *   y esa llamada interna corre dentro de la transacción ya abierta (Spring no intercepta llamadas a this).
 * - dispositivos.de(id).map(…).orElseGet(() → { … }): sobre el Optional del repositorio; si el celular existe lo
 *   convierte en dominio, y solo si no existe ejecuta el bloque que lo crea pendiente, lo guarda y publica el evento.
 *   orElseGet (no orElse) para que ese bloque no corra cuando el celular ya existe.
 * - Clock reloj y reloj.instant(): la hora viene del reloj del nodo inyectado.
 * - Map.of(): detalle vacío para la constancia de la bitácora.
 * - dispositivos.todos().stream().map(…).toList(): Stream; convierte cada fila en agregado.
 */
