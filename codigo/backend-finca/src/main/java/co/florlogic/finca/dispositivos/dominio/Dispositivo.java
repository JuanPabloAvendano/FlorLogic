package co.florlogic.finca.dispositivos.dominio;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Raíz de agregado: un celular conocido por el nodo (CT-10). Nace pendiente; el administrador lo aprueba o lo bloquea,
 * y puede cambiar de opinión. Cada cambio deja quién y cuándo.
 */
public final class Dispositivo {

    public enum Estado {
        PENDIENTE,
        APROBADO,
        BLOQUEADO
    }

    private final UUID id;
    private final Instant vistoPorPrimeraVez;
    private Estado estado;
    private Instant cambiadoEn;
    private String cambiadoPor;

    private Dispositivo(UUID id, Instant vistoPorPrimeraVez, Estado estado, Instant cambiadoEn, String cambiadoPor) {
        this.id = Objects.requireNonNull(id, "id");
        this.vistoPorPrimeraVez = Objects.requireNonNull(vistoPorPrimeraVez, "vistoPorPrimeraVez");
        this.estado = Objects.requireNonNull(estado, "estado");
        this.cambiadoEn = Objects.requireNonNull(cambiadoEn, "cambiadoEn");
        this.cambiadoPor = Objects.requireNonNull(cambiadoPor, "cambiadoPor");
    }

    /** El nodo lo ve por primera vez: queda pendiente, a nombre del nodo. */
    public static Dispositivo visto(UUID id, Instant ahora) {
        return new Dispositivo(id, ahora, Estado.PENDIENTE, ahora, "nodo");
    }

    public static Dispositivo desde(UUID id, Instant vistoPorPrimeraVez, Estado estado, Instant cambiadoEn,
            String cambiadoPor) {
        return new Dispositivo(id, vistoPorPrimeraVez, estado, cambiadoEn, cambiadoPor);
    }

    public void aprobar(String administrador, Instant ahora) {
        cambiar(Estado.APROBADO, administrador, ahora);
    }

    public void bloquear(String administrador, Instant ahora) {
        cambiar(Estado.BLOQUEADO, administrador, ahora);
    }

    private void cambiar(Estado nuevo, String administrador, Instant ahora) {
        estado = nuevo;
        cambiadoPor = Objects.requireNonNull(administrador, "administrador");
        cambiadoEn = Objects.requireNonNull(ahora, "ahora");
    }

    public UUID id() {
        return id;
    }

    public Instant vistoPorPrimeraVez() {
        return vistoPorPrimeraVez;
    }

    public Estado estado() {
        return estado;
    }

    public Instant cambiadoEn() {
        return cambiadoEn;
    }

    public String cambiadoPor() {
        return cambiadoPor;
    }
}
