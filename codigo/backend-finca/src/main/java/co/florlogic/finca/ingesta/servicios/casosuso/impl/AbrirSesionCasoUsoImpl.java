package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.dominio.sesion.ResultadoDeApertura;
import co.florlogic.finca.ingesta.dominio.sesion.SesionAbiertaValor;
import co.florlogic.finca.ingesta.dominio.sesion.SesionDeSincronizacion;
import co.florlogic.finca.ingesta.dominio.sesion.SesionId;
import co.florlogic.finca.ingesta.servicios.casosuso.AbrirSesionCasoUso;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.AvanceEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.SesionEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.SesionRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Auditoria;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.GeneradorDeIds;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.RegistroDeCelulares;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Reloj;

/**
 * Abre la sesión (recorrido de TX-1, pasos 3 a 6): reconoce al celular (uno bloqueado no entra; uno pendiente entra y
 * todo lo suyo queda marcado, CT-10), mide el desfase, la guarda, deja constancia en la bitácora y responde el punto de
 * reanudación.
 */
@Service
public class AbrirSesionCasoUsoImpl implements AbrirSesionCasoUso {

    private final SesionRepositorio sesiones;
    private final AvanceRepositorio avances;
    private final RegistroDeCelulares celulares;
    private final Auditoria auditoria;
    private final GeneradorDeIds ids;
    private final Reloj reloj;

    public AbrirSesionCasoUsoImpl(SesionRepositorio sesiones, AvanceRepositorio avances, RegistroDeCelulares celulares,
            Auditoria auditoria, GeneradorDeIds ids, Reloj reloj) {
        this.sesiones = sesiones;
        this.avances = avances;
        this.celulares = celulares;
        this.auditoria = auditoria;
        this.ids = ids;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public ResultadoDeApertura ejecutar(DispositivoId dispositivo, String persona, Instant relojDelCelular,
            int versionConfig) {
        RegistroDeCelulares.Estado estado = celulares.reconocer(dispositivo.valor());
        if (estado == RegistroDeCelulares.Estado.BLOQUEADO) {
            auditoria.registrar(persona, "sesion_rechazada", dispositivo.valor().toString(), Map.of("motivo", "dispositivo_bloqueado"));
            return new ResultadoDeApertura.DispositivoBloqueado();
        }
        SesionDeSincronizacion sesion = SesionDeSincronizacion.abrir(new SesionId(ids.nuevo()), dispositivo, persona,
                reloj.ahora(), relojDelCelular, versionConfig, estado == RegistroDeCelulares.Estado.APROBADO);
        sesiones.guardar(SesionEnsambladorSalida.aDto(sesion));
        auditoria.registrar(persona, "sesion_abierta", sesion.id().valor().toString(), Map.of(
                "dispositivo", dispositivo.valor().toString(), "desfase_ms", Long.toString(sesion.desfaseMs()),
                "dispositivo_estado", estado.name()));
        long confirmadoHasta = avances.deDispositivo(dispositivo.valor())
                .flatMap(AvanceEnsambladorSalida::aDominio)
                .flatMap(a -> a.confirmadoHasta().map(c -> c.valor()))
                .orElse(0L);
        return new ResultadoDeApertura.Abierta(new SesionAbiertaValor(sesion, confirmadoHasta));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service: Spring crea una instancia y la entrega a quien pida AbrirSesionCasoUso.
 * - @Transactional: todo ejecutar(...) corre en una sola transacción de base de datos; si algo lanza una excepción
 *   no comprobada, se deshace la sesión guardada y la bitácora.
 * - Map.of("clave", "valor", …): mapa pequeño y no modificable para el detalle de la bitácora.
 * - avances.deDispositivo(...).flatMap(AvanceEnsambladorSalida::aDominio).flatMap(a -> …).orElse(0L): cadena de
 *   Optional; si en cualquier paso no hay valor, el resultado es 0 (nada recibido).
 */
