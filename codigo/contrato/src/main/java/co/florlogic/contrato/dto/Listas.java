package co.florlogic.contrato.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Copias inmutables que nunca lanzan, para los constructores de los DTO (A-57). */
final class Listas {

    private Listas() {
    }

    /**
     * Ausente → vacía (A-33). Los elementos {@code null} se quitan en vez de lanzar como haría
     * {@code List.copyOf}: el árbol recibido conserva el defecto y el validador ya rechazó esa entrada
     * (A-55), así que aquí solo se evita que un {@code null} llegue a quien recorre la lista.
     */
    static <T> List<T> copia(List<T> lista) {
        if (lista == null) {
            return List.of();
        }
        List<T> sinNulos = new ArrayList<>(lista.size());
        for (T elemento : lista) {
            if (Objects.nonNull(elemento)) {
                sinNulos.add(elemento);
            }
        }
        return List.copyOf(sinNulos);
    }
}
