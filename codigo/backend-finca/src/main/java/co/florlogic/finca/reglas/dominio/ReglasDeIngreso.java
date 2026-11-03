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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - TreeSet: conjunto sin repetidos que se mantiene ordenado alfabéticamente; por eso los códigos salen siempre en el
 *   mismo orden («divergencia», «incompleta», «motivo_ausente»), sin importar en cuál se agregaron.
 * - campos.campos().containsValue(Presencia.FALTA): basta un campo esperado sin valor para marcar incompleta.
 * - marcadaEnElCelular != !marcas.isEmpty(): compara dos booleanos; es verdadero cuando el celular y el nodo no
 *   coinciden (uno marcó y el otro no). Se evalúa después de las otras reglas y antes de agregar divergencia.
 * - final y constructor privado: no se instancia; las reglas son funciones static sin estado.
 */
