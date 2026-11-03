package co.florlogic.finca.ingesta.servicios.casosuso;

import java.time.Instant;

import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.dominio.sesion.ResultadoDeApertura;

/** Caso de uso: abrir una sesión de sincronización (CT-1, CT-10; recorrido de TX-1, pasos 3 a 6). */
public interface AbrirSesionCasoUso {

    ResultadoDeApertura ejecutar(DispositivoId dispositivo, String persona, Instant relojDelCelular, int versionConfig);
}
