package co.florlogic.finca.ingesta.servicios.puertos.salida;

/** El doble cumple el mismo contrato que el adaptador JDBC. Unitaria: sin Spring ni base. */
class AnotacionesRecibidasEnMemoriaTest extends AnotacionesRecibidasContratoTest {

    @Override
    protected AnotacionesRecibidas crear() {
        return new AnotacionesRecibidasEnMemoria();
    }
}
