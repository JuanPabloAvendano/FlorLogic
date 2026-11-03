package co.florlogic.finca.bitacora.servicios.casosuso;

import java.util.Map;

/** Casos de uso de la bitácora (CT-11): registrar una entrada encadenada y verificar la cadena. */
public interface LlevarBitacoraCasoUso {

    void registrar(String actor, String evento, String objeto, Map<String, String> detalle);

    boolean cadenaIntegra();
}
