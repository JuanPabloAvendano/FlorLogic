package co.florlogic.finca.dispositivos.servicios.casosuso;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import co.florlogic.finca.dispositivos.dominio.AsignacionDelDia;

/** Caso de uso de la asignación del día (CT-13): el administrador dice qué le toca a cada celular. */
public interface AsignarCasoUso {

    /** @throws AsignacionDelDia.AsignacionInvalida si el celular no está aprobado o los ids no sirven */
    AsignacionDelDia asignar(LocalDate jornada, UUID dispositivo, List<String> secciones, List<String> campos,
            String administrador);

    List<AsignacionDelDia> de(LocalDate jornada);
}
