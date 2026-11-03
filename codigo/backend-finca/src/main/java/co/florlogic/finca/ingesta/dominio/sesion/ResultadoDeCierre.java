package co.florlogic.finca.ingesta.dominio.sesion;

/** Lo que devuelve el caso de uso de cerrar una sesión, con nombre en vez de excepciones (A-57). */
public sealed interface ResultadoDeCierre {

    record Cerrada(SesionDeSincronizacion sesion, long confirmadoHasta) implements ResultadoDeCierre {
    }

    record SesionDesconocida() implements ResultadoDeCierre {
    }

    record SesionAjena() implements ResultadoDeCierre {
    }
}
