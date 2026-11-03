package co.florlogic.finca.ciclo.servicios.casosuso.impl;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ciclo.servicios.casosuso.ResolverTrabajoCasoUso;
import co.florlogic.finca.ciclo.servicios.output.dto.TrabajoDto;
import co.florlogic.finca.ciclo.servicios.output.ports.repositorios.TrabajoRepositorio;
import co.florlogic.finca.ciclo.servicios.output.ports.serviciosexternos.Auditoria;

/**
 * La cola del gerente (F4): lista lo abierto y lo resuelve a su nombre, con constancia en la bitácora (CT-11). Dos
 * gerentes a la vez sobre el mismo trabajo: solo el primero lo resuelve y deja constancia.
 */
@Service
public class ResolverTrabajoCasoUsoImpl implements ResolverTrabajoCasoUso {

    /** Lo que cabe en una pantalla con sentido; el resto aparece al ir resolviendo. */
    static final int MAXIMO_EN_PANTALLA = 200;

    private final TrabajoRepositorio trabajos;
    private final Auditoria auditoria;
    private final Clock reloj;

    public ResolverTrabajoCasoUsoImpl(TrabajoRepositorio trabajos, Auditoria auditoria, Clock reloj) {
        this.trabajos = trabajos;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    @Override
    public List<TrabajoDto> abiertos() {
        return trabajos.abiertos(MAXIMO_EN_PANTALLA);
    }

    @Override
    @Transactional
    public boolean resolver(UUID anotacion, String gerente) {
        boolean resuelto = trabajos.resolver(anotacion, gerente, reloj.instant());
        if (resuelto) {
            auditoria.registrar(gerente, "trabajo_resuelto", anotacion.toString(), Map.of());
        }
        return resuelto;
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): Spring crea la instancia y la entrega a quien pida ResolverTrabajoCasoUso.
 * - @Transactional en resolver(…): cerrar el trabajo y escribir en la bitácora van en la misma transacción; si la
 *   bitácora falla, el trabajo no queda resuelto. abiertos() no la lleva: solo lee.
 * - static final int MAXIMO_EN_PANTALLA (sin public): constante de la clase, visible solo dentro de su paquete; hoy
 *   ninguna prueba la usa.
 * - Clock reloj y reloj.instant(): la hora de resolución viene del reloj del nodo inyectado, no de Instant.now().
 * - Map.of(): mapa vacío inmutable; la constancia de la bitácora no lleva detalle extra.
 */
