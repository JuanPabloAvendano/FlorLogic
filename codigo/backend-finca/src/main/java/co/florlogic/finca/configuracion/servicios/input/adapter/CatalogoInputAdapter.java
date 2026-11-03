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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): registra la clase como bean; Spring la crea una sola vez y le pasa por el constructor lo que pide (inyección por constructor, sin @Autowired). Implementa dos interfaces: el mismo bean atiende a quien pide FormasDeAnotacion y a quien pide VersionDeConfiguracion.
 * - YAGNI (del Javadoc): «You Aren't Gonna Need It», «no lo vas a necesitar»: no se agrega una capa hasta que haga falta.
 * - Optional.map(f -> ...): si llegó la fila la traduce a FormaDeAnotacion; si vino vacío, sigue vacío.
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
