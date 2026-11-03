package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ingesta.dominio.sesion.ResultadoDeCierre;
import co.florlogic.finca.ingesta.dominio.sesion.SesionDeSincronizacion;
import co.florlogic.finca.ingesta.servicios.casosuso.CerrarSesionCasoUso;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.AvanceEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.SesionEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.SesionRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Auditoria;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Reloj;

/**
 * Cierra la sesión, completa o parcial (recorrido de TX-1, paso 15). Cerrar otra vez una cerrada responde lo mismo:
 * el celular puede reintentar el cierre sin miedo.
 */
@Service
public class CerrarSesionCasoUsoImpl implements CerrarSesionCasoUso {

    private final SesionRepositorio sesiones;
    private final AvanceRepositorio avances;
    private final Auditoria auditoria;
    private final Reloj reloj;

    public CerrarSesionCasoUsoImpl(SesionRepositorio sesiones, AvanceRepositorio avances, Auditoria auditoria, Reloj reloj) {
        this.sesiones = sesiones;
        this.avances = avances;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public ResultadoDeCierre ejecutar(UUID sesionId, String persona, boolean completa) {
        Optional<SesionDeSincronizacion> guardada = sesiones.de(sesionId).flatMap(SesionEnsambladorSalida::aDominio);
        if (guardada.isEmpty()) {
            return new ResultadoDeCierre.SesionDesconocida();
        }
        SesionDeSincronizacion sesion = guardada.get();
        if (!sesion.esDe(persona)) {
            return new ResultadoDeCierre.SesionAjena();
        }
        boolean estabaAbierta = sesion.abierta();
        sesion.cerrar(completa, reloj.ahora());
        sesiones.guardar(SesionEnsambladorSalida.aDto(sesion));
        if (estabaAbierta) {
            auditoria.registrar(persona, "sesion_cerrada", sesionId.toString(), Map.of("estado", sesion.estado().name()));
        }
        long confirmadoHasta = avances.deDispositivo(sesion.dispositivo().valor())
                .flatMap(AvanceEnsambladorSalida::aDominio)
                .flatMap(a -> a.confirmadoHasta().map(c -> c.valor()))
                .orElse(0L);
        return new ResultadoDeCierre.Cerrada(sesion, confirmadoHasta);
    }
}
