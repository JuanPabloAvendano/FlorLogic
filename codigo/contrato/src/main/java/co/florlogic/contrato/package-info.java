/**
 * Contrato de ingesta de TX-1 ({@code ESPEC-contrato-ingesta.md}): los mensajes de
 * {@code POST /ingesta/v1/sesiones/{id}/particiones} como records (TEC-11), sus códigos y cabeceras.
 * La huella canónica (TEC-10) y el validador único llegan en los segmentos 3 a 6.
 *
 * <p>Patrones, con su fuente (D-15):
 * <ul>
 *   <li><b>Published Language</b>: el idioma documentado que comparten el celular y el nodo de finca;
 *       ninguno de los dos expone su modelo interno (<em>DDD Reference</em> · p. 43; Vernon,
 *       <em>Implementing DDD</em> · p. 143).</li>
 *   <li><b>Data Transfer Object</b>: los {@code dto} solo llevan datos y están «present on both sides»,
 *       por eso este módulo lo comparten los dos (<em>PoEAA</em> · secc. 100). El DTO tiene «little
 *       behavior» (<em>POSA 4</em> · p. 456): {@code incoherencias()} y {@code dato()} solo leen; las reglas
 *       de negocio van en el ingreso, nunca aquí.</li>
 *   <li><b>Tolerant Reader</b> y <b>Notification</b>, <b>Special Case</b>, <b>Null Object</b>: lo
 *       imprevisto vuelve como valor con nombre, nunca como excepción (A-57; ver {@link co.florlogic.contrato.lectura.LectorDto}).</li>
 * </ul>
 */
package co.florlogic.contrato;
