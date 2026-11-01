package co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos;

import java.util.UUID;

/**
 * Puerto de salida: qué celular atiende este nodo por ahora (ESPEC-dominio-ingesta A-107; un solo dispositivo
 * configurado hasta que llegue la identidad). Lo implementa la configuración, en infraestructura: el caso de uso no
 * sabe de dónde sale.
 */
public interface DispositivoDelNodo {

    UUID dispositivoId();
}
