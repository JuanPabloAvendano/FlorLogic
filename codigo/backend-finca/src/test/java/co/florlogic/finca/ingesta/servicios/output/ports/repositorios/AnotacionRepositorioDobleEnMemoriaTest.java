package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

/** El doble cumple el mismo contrato que el adaptador JDBC. Unitaria: sin Spring ni base. */
class AnotacionRepositorioDobleEnMemoriaTest extends AnotacionRepositorioContratoTest {

    @Override
    protected AnotacionRepositorio crear() {
        return new AnotacionRepositorioDobleEnMemoria();
    }
}
