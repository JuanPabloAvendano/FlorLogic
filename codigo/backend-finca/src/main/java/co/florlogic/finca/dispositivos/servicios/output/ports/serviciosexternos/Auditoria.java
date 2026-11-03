package co.florlogic.finca.dispositivos.servicios.output.ports.serviciosexternos;

import java.util.Map;

/** Puerto de salida hacia la bitácora (CT-11): deja constancia, sin datos de negocio, en la misma transacción. */
public interface Auditoria {

    void registrar(String actor, String evento, String objeto, Map<String, String> detalle);
}
