package co.florlogic.finca.bitacora.servicios.output.ports.repositorios;

import java.util.List;

import co.florlogic.finca.bitacora.servicios.output.dto.EntradaDto;

/** Puerto de salida de la bitácora. */
public interface BitacoraRepositorio {

    /**
     * El hash de la última entrada, tomando el turno para escribir hasta el fin de la transacción: dos registros a la
     * vez no pueden encadenar con la misma anterior. {@code null} si está vacía.
     */
    String ultimoHashConTurno();

    void insertar(EntradaDto entrada);

    /** Todas, en orden de inserción. */
    List<EntradaDto> todas();
}
