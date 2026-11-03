package co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * El ensamblador del controlador solo traduce (A-103): JSON del contrato → solicitud del puerto, y respuesta → resultado del contrato. Sin
 * Spring, sin base; el comportamiento HTTP de punta a punta lo cubren CanalParticionesTest y CanalPorGatewayTest.
 */
class ParticionEnsambladorTest {

    private static final String SESION = "0192f0a1-7c00-7000-8000-00000000a001";
    private static final UUID PARTICION = UUID.fromString("0192f0a1-7d10-7000-8000-00000000b001");
    private static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");
    private static final String PERSONA = "operario-1";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @Test
    void la_particion_del_canal_se_vuelve_request_con_la_sesion_de_la_ruta() throws IOException {
        Optional<ParticionLeidaDto> leida = ParticionEnsamblador.aSolicitud(SESION, peticion(), PERSONA);

        assertThat(leida).isPresent();
        assertThat(leida.get().particionUuid()).isEqualTo(PARTICION);
        assertThat(leida.get().solicitud().sesionId()).isEqualTo(UUID.fromString(SESION));
        assertThat(leida.get().solicitud().persona()).isEqualTo(PERSONA);
        assertThat(leida.get().solicitud().anotaciones()).hasSize(2);
        assertThat(leida.get().solicitud().anotaciones().getFirst().anotacionUuid()).isEqualTo(D001);
        assertThat(leida.get().solicitud().anotaciones().getFirst().consecutivo()).isEqualTo(1);
    }

    @Test
    void lo_que_no_se_puede_leer_sale_vacio_y_nunca_lanza() throws IOException {
        assertThat(ParticionEnsamblador.aSolicitud("no-es-uuid", peticion(), PERSONA)).isEmpty();
        assertThat(ParticionEnsamblador.aSolicitud(SESION, null, PERSONA)).isEmpty();
        assertThat(ParticionEnsamblador.aSolicitud(SESION, bytes("{roto"), PERSONA)).isEmpty();
        assertThat(ParticionEnsamblador.aSolicitud(SESION, bytes("[]"), PERSONA)).isEmpty();
        // Sin anotaciones el DTO daría una lista vacía y un 200 (formaMinima).
        assertThat(ParticionEnsamblador.aSolicitud(SESION,
                bytes("{\"contrato\":\"1.0\",\"particion_uuid\":\"" + PARTICION + "\",\"sesiones_captura\":[]}"), PERSONA)).isEmpty();
        // U+0000 es JSON válido pero jsonb no lo guarda (COH-105).
        String conNulo = new String(peticion(), StandardCharsets.UTF_8).replaceFirst("\"anotaciones\"\\s*:\\s*\\[\\s*\\{",
                "\"anotaciones\":[{\"x\\\\u0000\":1,");
        assertThat(ParticionEnsamblador.aSolicitud(SESION, bytes(conNulo), PERSONA)).isEmpty();
    }

    @Test
    void un_null_entre_las_anotaciones_se_rechaza_y_no_corre_el_emparejamiento() throws IOException {
        // [null, A, B]: el DTO quitaría el null y la anotación A quedaría con el JSON de otra (OBS-2-1).
        ObjectNode particion = particion();
        ((ArrayNode) particion.get("anotaciones")).insertNull(0);

        assertThat(ParticionEnsamblador.aSolicitud(SESION, bytes(particion), PERSONA)).isEmpty();
    }

    @Test
    void una_anotacion_que_no_es_objeto_se_rechaza() throws IOException {
        ObjectNode particion = particion();
        ((ArrayNode) particion.get("anotaciones")).add(5);

        assertThat(ParticionEnsamblador.aSolicitud(SESION, bytes(particion), PERSONA)).isEmpty();
    }

    @Test
    void una_anotacion_sin_anotacion_uuid_se_rechaza() throws IOException {
        ObjectNode particion = particion();
        ((ObjectNode) particion.get("anotaciones").get(0)).remove("anotacion_uuid");

        assertThat(ParticionEnsamblador.aSolicitud(SESION, bytes(particion), PERSONA)).isEmpty();
    }

    @Test
    void un_campo_que_el_contrato_no_conoce_se_rechaza() throws IOException {
        // El lector estricto (A-9) atrapa lo que sobra; la forma mínima sola lo dejaría pasar.
        ObjectNode particion = particion();
        particion.put("campo_raro", 1);

        assertThat(ParticionEnsamblador.aSolicitud(SESION, bytes(particion), PERSONA)).isEmpty();
    }

    @Test
    void la_respuesta_lleva_la_particion_los_conteos_y_el_reloj_del_nodo() {
        ResultadoParticionDto r = ParticionEnsamblador.aRespuesta(PARTICION, new RecibirParticionRespuestaDto(3, 2, 1, 7), 42L);

        assertThat(r.particionUuid()).isEqualTo(PARTICION);
        assertThat(r.recibidoMs()).isEqualTo(42L);
        assertThat(r.conteo().recibidas()).isEqualTo(3);
        assertThat(r.conteo().aplicadas()).isEqualTo(2);
        assertThat(r.conteo().sinEfecto()).isEqualTo(1);
    }

    private static byte[] peticion() throws IOException {
        try (InputStream in = ParticionEnsambladorTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return in.readAllBytes();
        }
    }

    private static ObjectNode particion() throws IOException {
        return (ObjectNode) JSON.readTree(peticion());
    }

    private static byte[] bytes(JsonNode arbol) {
        return bytes(arbol.toString());
    }

    private static byte[] bytes(String texto) {
        return texto.getBytes(StandardCharsets.UTF_8);
    }
}
