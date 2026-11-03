package co.florlogic.finca.ingesta.dominio.sesion;

/** En qué va una sesión de sincronización (CT-1). */
public enum EstadoSesion {
    /** Recibe particiones. */
    ABIERTA,
    /** El celular entregó todo lo pendiente. */
    CERRADA,
    /** El celular cerró sin entregar todo; el reintento abre otra y completa sin duplicar (TX-1.T 2). */
    PARCIAL
}
