package co.florlogic.finca.reglas.servicios.output.ports.serviciosexternos;

import co.florlogic.finca.reglas.dominio.CamposLeidos;
import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;

/** Puerto de salida: lee el JSON de una anotación según su forma, para que las reglas no sepan de JSON. */
public interface LectorDeContenido {

    CamposLeidos leer(String contenidoJson, FormaDto forma);
}
