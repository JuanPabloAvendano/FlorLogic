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
    void estanLosQuinceModulos() {
        assertThat(modulos.stream().map(ApplicationModule::getIdentifier).map(Object::toString))
                .containsExactlyInAnyOrder(
                        "bitacora", "ciclo", "configuracion", "consulta", "dispositivos",
                        "identidad", "ingesta", "llaves", "notificacion", "observabilidad",
                        "plataforma", "proyeccion", "reglas", "respaldo", "trabajos");
    }
}
