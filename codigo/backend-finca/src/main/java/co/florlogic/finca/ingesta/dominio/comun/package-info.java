/**
 * Ingreso de sincronización · lo común del dominio (ESPEC-dominio-ingesta A-76). No depende de ningún otro subpaquete;
 * Java puro: sin Spring, sin SQL, sin contrato. Junta tres clases de piezas porque las usan varios subpaquetes
 * ({@code anotacion}, {@code reglas}, {@code eventos}, {@code avance}) y separarlas crearía ciclos (K-116); Juan lo
 * mantuvo así el 21-oct (OBS-1-4):
 *
 * <ul>
 *   <li><b>Identidad</b> ({@code Id}): {@code AnotacionId}, {@code DispositivoId}.</li>
 *   <li><b>Objetos de valor</b> ({@code Valor}): {@code ConsecutivoValor}, {@code HuellaValor},
 *       {@code CapturaValor}.</li>
 *   <li><b>Vocabulario del juicio de ingreso</b> (enums): lo que <i>entra</i> al juicio de una anotación,
 *       {@code Presencia} (cómo viene cada campo), y lo que <i>sale</i>: {@code Resultado} (el veredicto),
 *       {@code CodigoMarca} (por qué quedó marcada; puede haber varios) y {@code MotivoRechazo} (por qué se rechazó;
 *       uno solo).</li>
 * </ul>
 */
package co.florlogic.finca.ingesta.dominio.comun;
