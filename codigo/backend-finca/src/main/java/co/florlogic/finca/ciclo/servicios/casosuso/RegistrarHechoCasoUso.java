package co.florlogic.finca.ciclo.servicios.casosuso;

import java.util.List;

import co.florlogic.finca.ciclo.dominio.HechoDeProduccion;

/** Caso de uso: actualizar el hecho de producción y abrir el trabajo del gerente si la anotación vino marcada. */
public interface RegistrarHechoCasoUso {

    void ejecutar(HechoDeProduccion hecho, List<String> codigos);
}
