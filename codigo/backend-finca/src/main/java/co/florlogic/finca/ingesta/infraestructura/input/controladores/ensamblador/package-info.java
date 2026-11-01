/**
 * Ingreso de sincronización · la traducción del contrato: el único lugar del backend que conoce el módulo {@code contrato}
 * y Jackson (ESPEC-dominio-ingesta A-103). El controlador habla HTTP; esto traduce el JSON del contrato a la solicitud del
 * puerto de entrada y la respuesta del puerto al JSON del contrato.
 */
package co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador;
