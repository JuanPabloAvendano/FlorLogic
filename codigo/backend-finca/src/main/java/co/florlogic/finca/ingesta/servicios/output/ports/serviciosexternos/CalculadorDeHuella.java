package co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos;

/**
 * Puerto de salida: la huella del contenido de una anotación (A-106). La calcula infraestructura con la forma canónica
 * del contrato; el caso de uso solo la pide.
 */
public interface CalculadorDeHuella {

    /** SHA-256 en hexadecimal minúsculo, versión {@code h1}. */
    String de(String contenidoJson);
}
