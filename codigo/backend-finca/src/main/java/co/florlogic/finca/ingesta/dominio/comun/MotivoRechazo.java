package co.florlogic.finca.ingesta.dominio.comun;

/**
 * Por qué una anotación queda {@code RECHAZADA} (ESPEC-dominio-ingesta A-68). En el cable los dos salen como
 * {@code datos_invalidos} (A-2, COH-116); la traducción es de la infraestructura.
 */
public enum MotivoRechazo {

    /** Su contenido se contradice (A-69). */
    DATOS_INVALIDOS,

    /** Repite un id ya guardado con otra huella (A-2): la guardada no cambia. */
    CORRUPTA
}
