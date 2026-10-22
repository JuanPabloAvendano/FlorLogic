package co.florlogic.finca.ingesta.servicios.output.port;

/** El doble cumple el mismo contrato que el adaptador JDBC. Unitaria: sin Spring ni base. */
class AnotacionesRecibidasDobleEnMemoriaTest extends AnotacionesRecibidasPuertoSalidaContratoTest {

    @Override
    protected AnotacionesRecibidasPuertoSalida crear() {
        return new AnotacionesRecibidasDobleEnMemoria();
    }
}
