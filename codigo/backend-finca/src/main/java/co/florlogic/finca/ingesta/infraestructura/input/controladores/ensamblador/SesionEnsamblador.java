package co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador;

import java.util.Optional;
import java.util.UUID;

import co.florlogic.contrato.dto.AbrirSesionDto;
import co.florlogic.contrato.dto.CerrarSesionDto;
import co.florlogic.contrato.dto.SesionAbiertaDto;
import co.florlogic.contrato.dto.SesionCerradaDto;
import co.florlogic.contrato.lectura.LectorDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.SesionAbiertaRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.SesionCerradaRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.AbrirSesionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.CerrarSesionSolicitudDto;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Ensamblador del controlador de la sesión: JSON del contrato ↔ DTO del puerto (A-103). Nunca lanza: lo ilegible o
 * incompleto es vacío y el controlador responde {@code 422} (A-57).
 */
public final class SesionEnsamblador {

    private static final ObjectMapper JSON = JsonMapper.builder().build();

    private SesionEnsamblador() {
    }

    public static Optional<AbrirSesionSolicitudDto> aAbrir(byte[] cuerpo, String persona) {
        return leer(cuerpo, AbrirSesionDto.class)
                .filter(d -> d.dispositivoUuid() != null && d.relojMs() != null && d.versionConfig() != null)
                .map(d -> new AbrirSesionSolicitudDto(d.dispositivoUuid(), persona, d.relojMs(), d.versionConfig()));
    }

    public static Optional<CerrarSesionSolicitudDto> aCerrar(String id, byte[] cuerpo, String persona) {
        Optional<UUID> sesion = uuid(id);
        if (sesion.isEmpty()) {
            return Optional.empty();
        }
        return leer(cuerpo, CerrarSesionDto.class)
                .map(d -> new CerrarSesionSolicitudDto(sesion.get(), persona, Boolean.TRUE.equals(d.completa())));
    }

    public static SesionAbiertaDto aAbierta(SesionAbiertaRespuestaDto r) {
        // marcada: siempre false hasta que P4 reconozca al celular (CT-10, ES-S13); el contrato ya la nombra.
        return new SesionAbiertaDto(r.sesionId(), r.desfaseMs(), r.confirmadoHasta(), r.versionConfig(), false);
    }

    public static SesionCerradaDto aCerrada(SesionCerradaRespuestaDto r) {
        return new SesionCerradaDto(r.sesionId(), r.estado(), r.confirmadoHasta(), r.versionConfig(), r.avisos());
    }

    private static <T> Optional<T> leer(byte[] cuerpo, Class<T> tipo) {
        if (cuerpo == null || cuerpo.length == 0) {
            return Optional.empty();
        }
        JsonNode arbol;
        try {
            arbol = JSON.readTree(cuerpo);
        } catch (JacksonException e) {
            return Optional.empty();
        }
        return LectorDto.estricto().leer(arbol, tipo) instanceof LecturaResultado.Leida<T> leida
                ? Optional.of(leida.valor())
                : Optional.empty();
    }

    private static Optional<UUID> uuid(String texto) {
        try {
            return Optional.of(UUID.fromString(texto));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
