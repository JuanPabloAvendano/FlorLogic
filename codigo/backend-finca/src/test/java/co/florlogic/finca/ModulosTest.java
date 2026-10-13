package co.florlogic.finca;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/** TEC-05: falla si un módulo toca la parte interna de otro. */
class ModulosTest {

    @Test
    void losModulosRespetanSusLimites() {
        ApplicationModules.of(BackendFinca.class).verify();
    }
}
