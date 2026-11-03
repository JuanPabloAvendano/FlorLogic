package co.florlogic.finca.ingesta.servicios.casosuso;

import java.util.UUID;

import co.florlogic.finca.ingesta.dominio.sesion.ResultadoDeCierre;

/** Caso de uso: cerrar una sesión, completa o parcial (CT-1; recorrido de TX-1, paso 15). */
public interface CerrarSesionCasoUso {

    ResultadoDeCierre ejecutar(UUID sesionId, String persona, boolean completa);
}
