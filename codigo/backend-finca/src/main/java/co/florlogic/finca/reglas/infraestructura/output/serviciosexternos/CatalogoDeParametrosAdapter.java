package co.florlogic.finca.reglas.infraestructura.output.serviciosexternos;

import java.util.Optional;

import org.springframework.stereotype.Component;

import co.florlogic.finca.configuracion.FormasDeAnotacion;
import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;
import co.florlogic.finca.reglas.servicios.output.ports.serviciosexternos.CatalogoDeParametros;

/** Llega al catálogo por la API pública de {@code configuracion} (A-95, A-96; Modulith: solo el paquete raíz). */
@Component
class CatalogoDeParametrosAdapter implements CatalogoDeParametros {

    private final FormasDeAnotacion formas;

    CatalogoDeParametrosAdapter(FormasDeAnotacion formas) {
        this.formas = formas;
    }

    @Override
    public Optional<FormaDto> forma(String tipo, int version) {
        return formas.de(tipo, version).map(f -> new FormaDto(f.campos(), f.campoMotivoAusencia()));
    }
}
