package co.florlogic.finca.dispositivos.servicios.casosuso;

import java.util.List;
import java.util.UUID;

import co.florlogic.finca.dispositivos.dominio.Dispositivo;

/** Casos de uso del registro de celulares (CT-10): reconocer al abrir sesión, aprobar y bloquear. */
public interface GestionarDispositivosCasoUso {

    Dispositivo reconocer(UUID id);

    void aprobar(UUID id, String administrador);

    void bloquear(UUID id, String administrador);

    List<Dispositivo> todos();
}
