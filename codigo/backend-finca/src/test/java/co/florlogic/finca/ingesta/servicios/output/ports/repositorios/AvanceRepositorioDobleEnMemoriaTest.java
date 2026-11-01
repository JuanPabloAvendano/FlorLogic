package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

class AvanceRepositorioDobleEnMemoriaTest extends AvanceRepositorioContratoTest {

    @Override
    protected AvanceRepositorio crear() {
        return new AvanceRepositorioDobleEnMemoria();
    }
}
