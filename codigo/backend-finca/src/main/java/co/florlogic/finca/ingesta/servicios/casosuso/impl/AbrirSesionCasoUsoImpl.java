package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.dominio.sesion.SesionAbiertaValor;
import co.florlogic.finca.ingesta.dominio.sesion.SesionDeSincronizacion;
import co.florlogic.finca.ingesta.dominio.sesion.SesionId;
import co.florlogic.finca.ingesta.servicios.casosuso.AbrirSesionCasoUso;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.AvanceEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.SesionEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.SesionRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.GeneradorDeIds;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Reloj;

/**
 * Abre la sesión (recorrido de TX-1, pasos 3 a 6): mide el desfase, la guarda y responde el punto de reanudación.
 * Reconocer al celular (pendiente, aprobado, bloqueado, CT-10) y dejar constancia en la bitácora llegan en P4.
 */
@Service
public class AbrirSesionCasoUsoImpl implements AbrirSesionCasoUso {

    private final SesionRepositorio sesiones;
    private final AvanceRepositorio avances;
    private final GeneradorDeIds ids;
    private final Reloj reloj;

    public AbrirSesionCasoUsoImpl(SesionRepositorio sesiones, AvanceRepositorio avances, GeneradorDeIds ids,
            Reloj reloj) {
        this.sesiones = sesiones;
        this.avances = avances;
        this.ids = ids;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public SesionAbiertaValor ejecutar(DispositivoId dispositivo, String persona, Instant relojDelCelular,
            int versionConfig) {
        SesionDeSincronizacion sesion = SesionDeSincronizacion.abrir(new SesionId(ids.nuevo()), dispositivo, persona,
                reloj.ahora(), relojDelCelular, versionConfig);
        sesiones.guardar(SesionEnsambladorSalida.aDto(sesion));
        long confirmadoHasta = avances.deDispositivo(dispositivo.valor())
                .flatMap(AvanceEnsambladorSalida::aDominio)
                .flatMap(a -> a.confirmadoHasta().map(c -> c.valor()))
                .orElse(0L);
        return new SesionAbiertaValor(sesion, confirmadoHasta);
    }
}
