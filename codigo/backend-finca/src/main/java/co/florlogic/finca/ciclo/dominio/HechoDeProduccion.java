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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable. Java genera el constructor con los nueve componentes, los métodos de lectura con
 *   el mismo nombre (capturadoEn(), anotacion()…), equals, hashCode y toString. Encaja con «objeto de valor»: dos
 *   hechos con los mismos datos son el mismo hecho.
 * - public HechoDeProduccion { … }: constructor compacto del record; corre antes de guardar los componentes y aquí
 *   solo vigila los obligatorios. valor, motivoAusencia y dispositivo pueden llegar null (un dato ausente trae motivo
 *   y no valor).
 * - Objects.requireNonNull(x, "nombre"): lanza NullPointerException con el nombre del componente si llega null, para
 *   que el error diga qué faltó.
 * - capturadoEn.compareTo(…): compara dos Instant y da negativo, cero o positivo; porHora > 0 quiere decir que este
 *   hecho se capturó después que el vigente.
 * - anotacion.compareTo(…): el desempate entre UUID. UUID.compareTo compara sus dos mitades de 64 bits como números con
 *   signo, no como texto; con ids de versión 7 (los de la prueba, que empiezan por la hora) el resultado coincide con
 *   el orden del texto.
 */
