package co.florlogic.contrato.lectura;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import co.florlogic.contrato.dto.ParticionDto;
import co.florlogic.contrato.dto.ResultadoParticionDto;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/** La barrera de lectura (A-57): ninguna entrada hace que leer lance. */
class LectorDtoTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();

    @Test
    void un_tipo_incorrecto_vuelve_como_fallo_con_su_pointer() {
        JsonNode arbol = mapper.readTree("""
                {"contrato":"1.0","particion_uuid":"0192f0a1-7d10-7000-8000-00000000b001",
                 "sesiones_captura":[],"anotaciones":[{"anotacion_uuid":"0192f0a0-2000-7000-8000-00000000d001",
                 "consecutivo":"uno"}]}""");

        LecturaResultado<ParticionDto> lectura = LectorDto.estricto().leer(arbol, ParticionDto.class);

        assertThat(lectura).isInstanceOfSatisfying(LecturaResultado.Fallida.class, fallo -> {
            assertThat(fallo.pointer()).isEqualTo("/anotaciones/0/consecutivo");
            assertThat(fallo.clase()).isEqualTo("formato_invalido");
        });
    }

    @Test
    void el_fallo_no_lleva_datos_de_negocio() {
        JsonNode arbol = mapper.readTree("{\"particion_uuid\":\"tallos-40-op07\"}");

        LecturaResultado<ParticionDto> lectura = LectorDto.estricto().leer(arbol, ParticionDto.class);

        // A-36: ni el valor recibido ni el mensaje de Jackson salen en el fallo.
        assertThat(lectura.toString()).doesNotContain("tallos-40-op07");
    }

    @Test
    void documento_ausente_o_null() {
        assertThat(LectorDto.estricto().leer(null, ParticionDto.class)).isInstanceOf(LecturaResultado.Fallida.class);
        assertThat(LectorDto.estricto().leer(mapper.readTree("null"), ParticionDto.class))
                .isInstanceOf(LecturaResultado.Fallida.class);
    }

    @Test
    void un_arreglo_donde_va_un_objeto_no_lanza() {
        LecturaResultado<ResultadoParticionDto> lectura = LectorDto.estricto().leer(mapper.readTree("[1,2,3]"), ResultadoParticionDto.class);

        assertThat(lectura).isInstanceOfSatisfying(LecturaResultado.Fallida.class,
                fallo -> assertThat(fallo.clase()).isEqualTo("tipo_incorrecto"));
    }

    @Test
    void lo_bien_formado_se_lee() {
        JsonNode arbol = mapper.readTree("""
                {"contrato":"1.0","particion_uuid":"0192f0a1-7d10-7000-8000-00000000b001",
                 "sesiones_captura":[],"anotaciones":[]}""");

        assertThat(LectorDto.estricto().leer(arbol, ParticionDto.class)).isInstanceOf(LecturaResultado.Leida.class);
    }

    @Test
    void el_estricto_acepta_la_peticion_dorada_y_su_captura_campo() throws java.io.IOException {
        JsonNode arbol = mapper.readTree(LectorDtoTest.class.getResourceAsStream("/dorados/peticion-particion.json"));

        LecturaResultado<ParticionDto> lectura = LectorDto.estricto().leer(arbol, ParticionDto.class);

        assertThat(lectura).isInstanceOf(LecturaResultado.Leida.class);
        for (JsonNode anotacion : arbol.get("anotaciones")) {
            assertThat(LectorDto.estricto().leer(anotacion.get("datos"), co.florlogic.contrato.dto.tipos.CapturaCampoDto.class))
                    .isInstanceOf(LecturaResultado.Leida.class);
        }
        JsonNode respuesta = mapper.readTree(LectorDtoTest.class.getResourceAsStream("/dorados/respuesta-particion.json"));
        assertThat(LectorDto.estricto().leer(respuesta, ResultadoParticionDto.class)).isInstanceOf(LecturaResultado.Leida.class);
    }

    @Test
    void el_estricto_rechaza_un_campo_desconocido_en_la_peticion() {
        JsonNode arbol = mapper.readTree("""
                {"contrato":"1.0","particion_uuid":"0192f0a1-7d10-7000-8000-00000000b001",
                 "sesiones_captura":[],"anotaciones":[],"cama_id":"B3-C17"}""");

        assertThat(LectorDto.estricto().leer(arbol, ParticionDto.class))
                .isEqualTo(new LecturaResultado.Fallida<>("/cama_id", "campo_desconocido"));
        // El tolerante lo ignora: por eso el nodo de finca nunca lee peticiones con él (A-32).
        assertThat(LectorDto.tolerante().leer(arbol, ParticionDto.class)).isInstanceOf(LecturaResultado.Leida.class);
    }

    @Test
    void el_tolerante_lee_una_respuesta_con_campos_de_una_version_mas_nueva() {
        JsonNode arbol = mapper.readTree("""
                {"particion_uuid":"0192f0a1-7d10-7000-8000-00000000b001","confirmado_hasta":1,"respaldado_hasta":0,
                 "recibido_ms":5,"conteo":{"recibidas":0,"aplicadas":0,"marcadas":0,"rechazadas":0,"sin_efecto":0},
                 "excepciones":[],"campo_de_la_v1_3":true}""");

        assertThat(LectorDto.tolerante().leer(arbol, ResultadoParticionDto.class)).isInstanceOf(LecturaResultado.Leida.class);
        assertThat(LectorDto.estricto().leer(arbol, ResultadoParticionDto.class))
                .isEqualTo(new LecturaResultado.Fallida<>("/campo_de_la_v1_3", "campo_desconocido"));
    }

    @Test
    void el_estricto_no_acepta_null_en_un_numero() {
        JsonNode arbol = mapper.readTree("""
                {"consecutivo":null,"motivo":"fallo_tecnico"}""");

        assertThat(LectorDto.estricto().leer(arbol, co.florlogic.contrato.dto.DescarteDto.class))
                .isInstanceOfSatisfying(LecturaResultado.Fallida.class, f -> assertThat(f.pointer()).isEqualTo("/consecutivo"));
    }

    @Test
    void el_pointer_escapa_segun_rfc_6901() {
        var ruta = java.util.List.of(
                new tools.jackson.core.JacksonException.Reference(null, "a/b~c"),
                new tools.jackson.core.JacksonException.Reference(null, 3));

        assertThat(LectorDto.pointer(ruta)).isEqualTo("/a~1b~0c/3");
        assertThat(LectorDto.pointer(java.util.List.of())).isEmpty();
    }
}
