package co.florlogic.finca.reglas.dominio;

import java.util.Set;
import java.util.TreeSet;

/**
 * Las reglas del servidor para una anotación (ESPEC-dominio-ingesta A-99, A-101; contrato A-32, A-35). Java puro:
 * reciben los campos ya leídos y devuelven los códigos del contrato.
 *
 * <ul>
 *   <li><b>Dato ausente:</b> falta un valor → {@code incompleta}; y si además no trae motivo → {@code motivo_ausente}.
 *       Se marca, no se rechaza: lo incompleto queda como trabajo del gerente (P-S01).</li>
 *   <li><b>Divergencia:</b> el celular la marcó y el nodo no, o al revés → {@code divergencia} (recorrido, paso 10).</li>
 * </ul>
 */
public final class ReglasDeIngreso {

    public static final String INCOMPLETA = "incompleta";
    public static final String MOTIVO_AUSENTE = "motivo_ausente";
    public static final String DIVERGENCIA = "divergencia";

    private ReglasDeIngreso() {
    }

    public static Set<String> marcas(CamposLeidos campos, boolean marcadaEnElCelular) {
        Set<String> marcas = new TreeSet<>();
        if (campos.campos().containsValue(Presencia.FALTA)) {
            marcas.add(INCOMPLETA);
            if (!campos.hayMotivo()) {
                marcas.add(MOTIVO_AUSENTE);
            }
        }
        if (marcadaEnElCelular != !marcas.isEmpty()) {
            marcas.add(DIVERGENCIA);
        }
        return marcas;
    }
}
