package co.florlogic.finca.configuracion;

/**
 * API pública: la versión de configuración vigente del nodo (ADR-029). La sesión la devuelve al abrir y al cerrar para
 * que el celular sepa si debe descargar una más nueva.
 */
public interface VersionDeConfiguracion {

    int vigente();
}
