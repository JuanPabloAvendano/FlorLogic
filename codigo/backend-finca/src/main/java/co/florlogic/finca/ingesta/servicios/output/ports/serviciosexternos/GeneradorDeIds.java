package co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos;

import java.util.UUID;

/** Puerto de salida: ids nuevos, UUID v7 (TEC-12; ESPEC-dominio-ingesta §5). */
public interface GeneradorDeIds {

    UUID nuevo();
}
