package co.florlogic.finca.configuracion.servicios.input.adapter;

import java.util.Optional;

import org.springframework.stereotype.Service;

import co.florlogic.finca.configuracion.FormaDeAnotacion;
import co.florlogic.finca.configuracion.FormasDeAnotacion;
import co.florlogic.finca.configuracion.VersionDeConfiguracion;
import co.florlogic.finca.configuracion.servicios.output.ports.repositorios.CatalogoRepositorio;

/**
 * Implementa la API pública del módulo leyendo el catálogo por su puerto. Es una consulta sin reglas, así que no hay
 * caso de uso en medio (YAGNI): el adaptador solo traduce la fila a la forma pública.
 */
@Service
public class CatalogoInputAdapter implements FormasDeAnotacion, VersionDeConfiguracion {

    private final CatalogoRepositorio catalogo;

    public CatalogoInputAdapter(CatalogoRepositorio catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public Optional<FormaDeAnotacion> de(String tipo, int version) {
        return catalogo.forma(tipo, version)
                .map(f -> new FormaDeAnotacion(f.tipo(), f.version(), f.campos(), f.campoMotivoAusencia()));
    }

    @Override
    public int vigente() {
        return catalogo.versionVigente();
    }
}
