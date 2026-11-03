package co.florlogic.finca;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

/** TEC-05: falla si un módulo toca la parte interna de otro. */
class ModulosTest {

    private final ApplicationModules modulos = ApplicationModules.of(BackendFincaAplicacion.class);

    @Test
    void losModulosRespetanSusLimites() {
        modulos.verify();
    }

    @Test
    void estanLosDiecisieteModulos() {
        assertThat(modulos.stream().map(ApplicationModule::getIdentifier).map(Object::toString))
                .containsExactlyInAnyOrder(
                        "dominio", "infraestructura", "transversal",
                        "bitacora", "ciclo", "configuracion", "consulta", "dispositivos",
                        "identidad", "ingesta", "llaves", "notificacion", "observabilidad",
                        "proyeccion", "reglas", "respaldo", "trabajos");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - ApplicationModules.of(BackendFincaAplicacion.class) (Spring Modulith): recorre el paquete de la aplicación y
 *   arma el mapa de módulos (cada subpaquete directo de co.florlogic.finca) y de quién usa a quién.
 * - modulos.verify(): falla si hay ciclos entre módulos o si un módulo usa una clase que no está en el paquete
 *   raíz (la API pública) de otro.
 * - @Test: método que JUnit 5 ejecuta como prueba.
 * - modulos.stream().map(ApplicationModule::getIdentifier).map(Object::toString): saca el nombre de cada módulo
 *   como texto. ApplicationModule::getIdentifier es una referencia a método (equivale a m -> m.getIdentifier()).
 * - assertThat(…).containsExactlyInAnyOrder(…) (AssertJ): exige exactamente esos diecisiete nombres, ni uno más ni
 *   uno menos, en cualquier orden; un módulo nuevo o borrado obliga a tocar esta lista a propósito.
 */
