package co.florlogic.finca.ingesta.dominio.sesion;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;

/**
 * Raíz de agregado: una sesión de sincronización de un celular (CT-1). Protege «solo una sesión abierta recibe
 * particiones» y «se cierra una sola vez». El desfase se mide al abrir: reloj del celular menos reloj del nodo
 * (ADR-031; positivo si el celular va adelantado).
 */
public final class SesionDeSincronizacion {

    private final SesionId id;
    private final DispositivoId dispositivo;
    private final String persona;
    private final Instant abiertaEn;
    private final long desfaseMs;
    private final int versionConfig;
    private final boolean dispositivoRegistrado;
    private EstadoSesion estado;
    private Instant cerradaEn;

    private SesionDeSincronizacion(SesionId id, DispositivoId dispositivo, String persona, Instant abiertaEn,
            long desfaseMs, int versionConfig, boolean dispositivoRegistrado, EstadoSesion estado, Instant cerradaEn) {
        this.id = Objects.requireNonNull(id, "id");
        this.dispositivo = Objects.requireNonNull(dispositivo, "dispositivo");
        this.persona = Objects.requireNonNull(persona, "persona");
        this.abiertaEn = Objects.requireNonNull(abiertaEn, "abiertaEn");
        this.desfaseMs = desfaseMs;
        this.versionConfig = versionConfig;
        this.dispositivoRegistrado = dispositivoRegistrado;
        this.estado = Objects.requireNonNull(estado, "estado");
        this.cerradaEn = cerradaEn;
    }

    /**
     * Abre una sesión nueva. Si el celular no está registrado, la sesión igual entra, pero marcada (ES-S13): lo
     * capturado no se pierde y el administrador lo revisa.
     */
    public static SesionDeSincronizacion abrir(SesionId id, DispositivoId dispositivo, String persona, Instant ahora,
            Instant relojDelCelular, int versionConfig, boolean dispositivoRegistrado) {
        long desfase = Duration.between(ahora, relojDelCelular).toMillis();
        return new SesionDeSincronizacion(id, dispositivo, persona, ahora, desfase, versionConfig, dispositivoRegistrado,
                EstadoSesion.ABIERTA, null);
    }

    /** Rehace una sesión guardada, sin juzgar. Vacío si lo guardado no es coherente (cerrada sin fecha, o al revés). */
    public static Optional<SesionDeSincronizacion> desde(SesionId id, DispositivoId dispositivo, String persona,
            Instant abiertaEn, long desfaseMs, int versionConfig, boolean dispositivoRegistrado, EstadoSesion estado,
            Instant cerradaEn) {
        if (id == null || dispositivo == null || persona == null || abiertaEn == null || estado == null
                || (estado == EstadoSesion.ABIERTA) != (cerradaEn == null)) {
            return Optional.empty();
        }
        return Optional.of(new SesionDeSincronizacion(id, dispositivo, persona, abiertaEn, desfaseMs, versionConfig,
                dispositivoRegistrado, estado, cerradaEn));
    }

    public boolean abierta() {
        return estado == EstadoSesion.ABIERTA;
    }

    /** Solo quien la abrió entrega en ella (contrato A-51: {@code 403 sesion_ajena}). */
    public boolean esDe(String otraPersona) {
        return persona.equals(otraPersona);
    }

    /** Cierra una sola vez; cerrar otra vez no cambia nada (idempotente: el celular puede reintentar el cierre). */
    public void cerrar(boolean completa, Instant ahora) {
        if (!abierta()) {
            return;
        }
        estado = completa ? EstadoSesion.CERRADA : EstadoSesion.PARCIAL;
        cerradaEn = Objects.requireNonNull(ahora, "ahora");
    }

    public SesionId id() {
        return id;
    }

    public DispositivoId dispositivo() {
        return dispositivo;
    }

    public String persona() {
        return persona;
    }

    public Instant abiertaEn() {
        return abiertaEn;
    }

    public long desfaseMs() {
        return desfaseMs;
    }

    public int versionConfig() {
        return versionConfig;
    }

    public boolean dispositivoRegistrado() {
        return dispositivoRegistrado;
    }

    public EstadoSesion estado() {
        return estado;
    }

    public Optional<Instant> cerradaEn() {
        return Optional.ofNullable(cerradaEn);
    }
}
