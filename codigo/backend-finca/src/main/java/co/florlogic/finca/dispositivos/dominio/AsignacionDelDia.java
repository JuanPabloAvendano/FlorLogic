package co.florlogic.finca.dispositivos.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Lo que le toca a un celular en una jornada (CT-13): todas las celdas sección × campo que el administrador le asigna.
 * Solo un celular aprobado recibe asignación. Sin secciones o sin campos, la asignación queda vacía (se le quita todo).
 */
public final class AsignacionDelDia {

    /** Una celda: un campo de una sección. */
    public record Celda(String seccion, String campo) {
    }

    /** Lo que el administrador pidió no se puede asignar; el mensaje se le muestra tal cual. */
    public static final class AsignacionInvalida extends RuntimeException {
        public AsignacionInvalida(String mensaje) {
            super(mensaje);
        }
    }

    /** Las celdas de un celular en un día: más que esto no se recorre en una jornada (y frena un formulario abusivo). */
    public static final int MAXIMO_DE_CELDAS = 500;
    private static final Pattern ID = Pattern.compile("[A-Za-z0-9._-]{1,64}");

    private final LocalDate jornada;
    private final UUID dispositivo;
    private final List<Celda> celdas;

    private AsignacionDelDia(LocalDate jornada, UUID dispositivo, List<Celda> celdas) {
        this.jornada = jornada;
        this.dispositivo = dispositivo;
        this.celdas = List.copyOf(celdas);
    }

    public static AsignacionDelDia para(LocalDate jornada, Dispositivo celular, Collection<String> secciones,
            Collection<String> campos) {
        Objects.requireNonNull(jornada, "jornada");
        Objects.requireNonNull(celular, "celular");
        if (celular.estado() != Dispositivo.Estado.APROBADO) {
            throw new AsignacionInvalida("Solo un celular aprobado recibe asignación; este está " + celular.estado());
        }
        Set<String> s = ids(secciones, "sección");
        Set<String> c = ids(campos, "campo");
        if ((long) s.size() * c.size() > MAXIMO_DE_CELDAS) {
            throw new AsignacionInvalida("Son " + s.size() * c.size() + " celdas; el máximo por celular y día es "
                    + MAXIMO_DE_CELDAS);
        }
        List<Celda> celdas = new ArrayList<>();
        for (String seccion : s) {
            for (String campo : c) {
                celdas.add(new Celda(seccion, campo));
            }
        }
        return new AsignacionDelDia(jornada, celular.id(), celdas);
    }

    /** La asignación tal como quedó guardada: ya pasó las reglas al asignarse. */
    public static AsignacionDelDia leida(LocalDate jornada, UUID dispositivo, List<Celda> celdas) {
        return new AsignacionDelDia(Objects.requireNonNull(jornada, "jornada"),
                Objects.requireNonNull(dispositivo, "dispositivo"), celdas);
    }

    /** Quita espacios y repetidos y conserva el orden; un id con caracteres raros se rechaza con su nombre. */
    private static Set<String> ids(Collection<String> crudos, String que) {
        Set<String> limpios = new LinkedHashSet<>();
        for (String crudo : crudos == null ? List.<String>of() : crudos) {
            String id = crudo == null ? "" : crudo.strip();
            if (id.isEmpty()) {
                continue;
            }
            if (!ID.matcher(id).matches()) {
                throw new AsignacionInvalida("Id de " + que + " no válido (letras, números, punto, guion; hasta 64)");
            }
            limpios.add(id);
        }
        return limpios;
    }

    public LocalDate jornada() {
        return jornada;
    }

    public UUID dispositivo() {
        return dispositivo;
    }

    public List<Celda> celdas() {
        return celdas;
    }

    /** Las secciones de la asignación, sin repetir y en orden. */
    public List<String> secciones() {
        return celdas.stream().map(Celda::seccion).distinct().toList();
    }

    public List<String> campos() {
        return celdas.stream().map(Celda::campo).distinct().toList();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - public record Celda(String seccion, String campo): record anidado; una pareja inmutable con equals por valor,
 *   así dos celdas con la misma sección y el mismo campo son iguales (lo usa la prueba con containsExactly).
 * - public static final class AsignacionInvalida extends RuntimeException: excepción propia del dominio, anidada y
 *   static (no necesita una AsignacionDelDia para existir). Extiende RuntimeException para no obligar a declararla
 *   con throws; el adaptador de entrada la atrapa y la traduce.
 * - Pattern.compile("[A-Za-z0-9._-]{1,64}") (expresión regular): se compila una vez y se reutiliza. Acepta de 1 a 64
 *   letras sin tilde, dígitos, punto, guion bajo o guion; ID.matcher(id).matches() exige que todo el id cumpla.
 * - Objects.requireNonNull(x, "nombre"): lanza NullPointerException con ese nombre si llega null.
 * - (long) s.size() * c.size(): multiplica en long para que un producto enorme no se desborde del int antes de
 *   compararlo con el máximo.
 * - LinkedHashSet: conjunto que no admite repetidos y conserva el orden en que se agregaron; por eso «B1, B2, B1»
 *   queda «B1, B2».
 * - crudos == null ? List.<String>of() : crudos: si no llegó colección se recorre una lista vacía; el <String>
 *   explícito le dice a Java el tipo de la lista vacía.
 * - crudo.strip(): quita espacios (también los Unicode) al principio y al final.
 * - List.copyOf(celdas) en el constructor: guarda una copia inmutable; quien tenía la lista original no puede
 *   cambiar la asignación.
 * - celdas.stream().map(Celda::seccion).distinct().toList(): Stream; toma la sección de cada celda (Celda::seccion es
 *   una referencia a método, igual a c → c.seccion()), quita repetidos conservando el orden y devuelve una lista
 *   inmutable.
 */
