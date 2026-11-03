package co.florlogic.finca.ciclo.dominio;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Objeto de valor: el valor vigente de un campo de una sección en una jornada (CT-12). Entre dos capturas del mismo
 * hecho gana la más reciente por la hora del celular corregida (RF-022, P-S02: no media nadie); si empatan, el id de
 * anotación mayor, para que el resultado no dependa del orden de llegada.
 */
public record HechoDeProduccion(String produccionId, String seccionId, String campoId, LocalDate jornada, String valor,
        String motivoAusencia, Instant capturadoEn, UUID anotacion, UUID dispositivo) {

    public HechoDeProduccion {
        Objects.requireNonNull(produccionId, "produccionId");
        Objects.requireNonNull(seccionId, "seccionId");
        Objects.requireNonNull(campoId, "campoId");
        Objects.requireNonNull(jornada, "jornada");
        Objects.requireNonNull(capturadoEn, "capturadoEn");
        Objects.requireNonNull(anotacion, "anotacion");
    }

    /** {@code true} si este hecho debe quedar en lugar del vigente. */
    public boolean reemplazaA(HechoDeProduccion vigente) {
        int porHora = capturadoEn.compareTo(vigente.capturadoEn());
        return porHora > 0 || (porHora == 0 && anotacion.compareTo(vigente.anotacion()) > 0);
    }
}
