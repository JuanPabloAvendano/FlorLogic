package co.florlogic.contrato.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.contrato.codigos.CodigoIngreso;
import co.florlogic.contrato.dto.tipos.CapturaCampoDto;
import co.florlogic.contrato.dto.tipos.ValorDto;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * La forma de los mensajes contra los ejemplos dorados de la ESPEC (§4b). Solo prueba la forma:
 * la validación, los topes y la huella tienen sus pruebas en los segmentos 3 a 6.
 */
class DtoJsonTest {

    // Mapper sin configurar a propósito: la forma tiene que salir de las anotaciones de los records,
    // no de una configuración que el simulador o el nodo de finca podrían no repetir.
    private final ObjectMapper mapper = JsonMapper.builder().build();

    @Test
    void lee_la_peticion_dorada() throws IOException {
        ParticionDto peticion = mapper.readValue(dorado("peticion-particion.json"), ParticionDto.class);

        assertThat(peticion.contrato()).isEqualTo("1.0");
        assertThat(peticion.particionUuid()).isEqualTo(UUID.fromString("0192f0a1-7d10-7000-8000-00000000b001"));
        assertThat(peticion.descartes()).isEmpty();
        assertThat(peticion.primerosAcuses()).isEmpty();

        SesionCapturaDto sesion = peticion.sesionesCaptura().getFirst();
        assertThat(sesion.personaId()).isEqualTo("op-07");
        assertThat(sesion.inicio()).isEqualTo(Instant.parse("2026-10-16T06:02:11Z"));

        AnotacionDto primera = peticion.anotaciones().getFirst();
        assertThat(primera.consecutivo()).isEqualTo(1);
        assertThat(primera.tipo()).isEqualTo(CapturaCampoDto.TIPO);
        assertThat(primera.veredictoLocal()).isEqualTo(AnotacionDto.VeredictoLocal.ACEPTADA);
        assertThat(primera.sesionCapturaUuid()).isEqualTo(sesion.uuid());
        assertThat(primera.tiempo().desfaseDeclarado()).isEqualTo(new DesfaseDto.Conocido(-1200));
    }

    @Test
    void los_datos_de_captura_campo_dan_el_valor_o_su_falta() throws IOException {
        ParticionDto peticion = mapper.readValue(dorado("peticion-particion.json"), ParticionDto.class);

        CapturaCampoDto con = mapper.treeToValue(peticion.anotaciones().get(0).datos(), CapturaCampoDto.class);
        assertThat(con.jornada()).isEqualTo(LocalDate.of(2026, 10, 16));
        assertThat(con.dato()).isInstanceOfSatisfying(ValorDto.Hay.class,
                hay -> assertThat(hay.valor().asInt()).isEqualTo(40));

        // "valor": null en el JSON = dato ausente (A-33, A-35)
        CapturaCampoDto sin = mapper.treeToValue(peticion.anotaciones().get(1).datos(), CapturaCampoDto.class);
        assertThat(sin.valor()).isNull();
        assertThat(sin.dato()).isEqualTo(new ValorDto.Falta(new ValorDto.Motivo.Declarado("no_disponible")));
    }

    @Test
    void valor_ausente_y_valor_null_son_lo_mismo() {
        String base = """
                {"produccion_id":"P-1","seccion_id":"S-1","campo_id":"c","jornada":"2026-10-16"%s}""";
        CapturaCampoDto ausente = mapper.readValue(base.formatted(""), CapturaCampoDto.class);
        CapturaCampoDto nulo = mapper.readValue(base.formatted(",\"valor\":null"), CapturaCampoDto.class);

        assertThat(nulo).isEqualTo(ausente);
        assertThat(nulo.dato()).isEqualTo(new ValorDto.Falta(ValorDto.Motivo.SIN_MOTIVO));
    }

    @Test
    void valor_con_motivo_de_ausencia_es_contradictorio_y_no_lanza() {
        CapturaCampoDto ambos = new CapturaCampoDto("P-1", "S-1", "c", LocalDate.of(2026, 10, 16),
                mapper.getNodeFactory().numberNode(3), "no_disponible", null);

        assertThat(ambos.dato()).isInstanceOf(ValorDto.Contradictorio.class);
    }

    @Test
    void desfase_ausente_null_y_numero() {
        TiempoDto ausente = mapper.readValue("{\"crudo_ms\":1,\"mono_ms\":2,\"arranque\":3}", TiempoDto.class);
        TiempoDto nulo = mapper.readValue(
                "{\"crudo_ms\":1,\"mono_ms\":2,\"arranque\":3,\"desfase_declarado_ms\":null}", TiempoDto.class);
        TiempoDto conocido = mapper.readValue(
                "{\"crudo_ms\":1,\"mono_ms\":2,\"arranque\":3,\"desfase_declarado_ms\":-5}", TiempoDto.class);

        assertThat(ausente.desfaseDeclarado()).isEqualTo(DesfaseDto.DESCONOCIDO);
        assertThat(nulo).isEqualTo(ausente);
        assertThat(conocido.desfaseDeclarado()).isEqualTo(new DesfaseDto.Conocido(-5));

        // El desconocido no se escribe: null y ausente valen lo mismo, y omitir es la forma única (A-33)
        assertThat(mapper.readTree(mapper.writeValueAsString(ausente)).has("desfase_declarado_ms")).isFalse();
        assertThat(mapper.readTree(mapper.writeValueAsString(conocido)).get("desfase_declarado_ms").asLong())
                .isEqualTo(-5);
    }

    @Test
    void la_peticion_dorada_vuelve_igual() throws IOException {
        JsonNode original = mapper.readTree(dorado("peticion-particion.json"));
        ParticionDto peticion = mapper.treeToValue(original, ParticionDto.class);

        // datos viaja como árbol crudo (A-15): conserva su "valor": null tal como llegó.
        JsonNode escrita = mapper.valueToTree(peticion);
        assertThat(escrita.equals(MISMO_NUMERO, original)).as("%s", escrita).isTrue();
    }

    @Test
    void captura_campo_escrita_omite_el_valor_ausente() {
        CapturaCampoDto sin = new CapturaCampoDto("P-1", "S-1", "c", LocalDate.of(2026, 10, 16), null, "no_disponible", null);

        JsonNode escrita = mapper.valueToTree(sin);
        assertThat(escrita.has("valor")).isFalse();
        assertThat(escrita.has("motivo_desviacion")).isFalse();
        assertThat(escrita.get("jornada").asString()).isEqualTo("2026-10-16");
    }

    @Test
    void la_respuesta_dorada_vuelve_igual() throws IOException {
        JsonNode original = mapper.readTree(dorado("respuesta-particion.json"));
        ResultadoParticionDto respuesta = mapper.treeToValue(original, ResultadoParticionDto.class);

        assertThat(respuesta.conteo()).isEqualTo(new ConteoDto(2, 2, 1, 0, 0));
        assertThat(respuesta.excepciones().getFirst().codigos()).containsExactly(CodigoIngreso.INCOMPLETA.id());
        assertThat(respuesta.descartesAcusados()).isEmpty();
        assertThat(respuesta.accion()).isNull();
        JsonNode escrita = mapper.valueToTree(respuesta);
        assertThat(escrita.equals(MISMO_NUMERO, original)).as("%s", escrita).isTrue();
    }

    @Test
    void la_respuesta_que_rompe_sus_invariantes_se_arma_y_dice_que_rompe() {
        UUID id = UUID.fromString("0192f0a1-7d10-7000-8000-00000000b001");

        assertThat(new ConteoDto(2, 2, 1, 0, 0).incoherencias()).isEmpty();
        assertThat(new ConteoDto(3, 2, 0, 0, 0).incoherencias()).containsExactly("conteo_no_suma");
        assertThat(new ConteoDto(2, 2, 3, 0, 0).incoherencias()).containsExactly("marcadas_mayor_que_aplicadas");
        assertThat(new ResultadoParticionDto(id, 2, 5, 0, new ConteoDto(2, 2, 1, 0, 0), List.of(), null, null).incoherencias())
                .containsExactly("respaldado_mayor_que_confirmado");
        assertThat(new ResultadoParticionDto(null, 0, 0, 0, null, null, null, null).incoherencias())
                .containsExactly("particion_uuid_ausente", "conteo_ausente");
    }

    @Test
    void una_respuesta_mas_nueva_se_sigue_leyendo() {
        String json = """
                {"particion_uuid":"0192f0a1-7d10-7000-8000-00000000b001","confirmado_hasta":1,"respaldado_hasta":0,
                 "recibido_ms":5,"conteo":{"recibidas":1,"aplicadas":0,"marcadas":0,"rechazadas":0,"sin_efecto":1},
                 "excepciones":[{"anotacion_uuid":"0192f0a0-2000-7000-8000-00000000d001","resultado":"aplazada",
                                 "codigos":["codigo_del_futuro","R-RIEGO-12"]}],
                 "accion":"reiniciar_reloj"}""";

        ResultadoParticionDto respuesta = mapper.readValue(json, ResultadoParticionDto.class);

        assertThat(respuesta.excepciones().getFirst().resultado()).isEqualTo(ExcepcionDto.Resultado.DESCONOCIDO);
        assertThat(respuesta.excepciones().getFirst().codigos()).containsExactly("codigo_del_futuro", "R-RIEGO-12");
        assertThat(respuesta.accion()).isEqualTo(ResultadoParticionDto.Accion.DESCONOCIDA);
        assertThat(respuesta.incoherencias()).containsExactly("resultado_desconocido", "accion_desconocida");
    }

    @Test
    void un_veredicto_o_motivo_desconocido_no_tumba_la_lectura() {
        assertThat(AnotacionDto.VeredictoLocal.de("dudosa")).isEqualTo(AnotacionDto.VeredictoLocal.DESCONOCIDO);
        assertThat(AnotacionDto.VeredictoLocal.de(null)).isEqualTo(AnotacionDto.VeredictoLocal.DESCONOCIDO);
        assertThat(mapper.readValue("{\"consecutivo\":4,\"motivo\":\"se_cayo\"}", DescarteDto.class).motivo())
                .isEqualTo(DescarteDto.Motivo.DESCONOCIDO);
        assertThat(CodigoIngreso.de("incompleta")).contains(CodigoIngreso.INCOMPLETA);
        assertThat(CodigoIngreso.de("R-RIEGO-12")).isEmpty();
    }

    @Test
    void una_lista_con_null_no_lanza() {
        List<ExcepcionDto> conHueco = new java.util.ArrayList<>();
        conHueco.add(null);
        ResultadoParticionDto r = new ResultadoParticionDto(null, 0, 0, 0, null, conHueco, null, null);

        assertThat(r.excepciones()).isEmpty();
    }

    @Test
    void sin_excepciones_igual_escribe_el_arreglo_vacio() {
        ResultadoParticionDto limpio = new ResultadoParticionDto(
                UUID.fromString("0192f0a1-7d10-7000-8000-00000000b001"), 2, 0, 1L, new ConteoDto(2, 2, 0, 0, 0),
                null, null, null);

        JsonNode escrito = mapper.valueToTree(limpio);
        assertThat(escrito.get("excepciones").isArray()).isTrue();
        assertThat(escrito.has("descartes_acusados")).isFalse();
        assertThat(escrito.has("accion")).isFalse();
        assertThat(escrito.get("respaldado_hasta").asLong()).isZero();
    }

    @Test
    void cada_codigo_del_ingreso_cumple_la_forma_de_3b() {
        for (CodigoIngreso codigo : CodigoIngreso.values()) {
            assertThat(codigo.id()).matches("^[a-z_]{1,40}$");
        }
    }

    /**
     * JsonNode distingue 2 (int) de 2 (long) aunque el texto sea igual; aquí solo importa el valor.
     * Devuelve 0 cuando los dos nodos se consideran iguales.
     */
    private static final Comparator<JsonNode> MISMO_NUMERO = (a, b) -> {
        if (a.equals(b)) {
            return 0;
        }
        return a.isNumber() && b.isNumber() && a.decimalValue().compareTo(b.decimalValue()) == 0 ? 0 : 1;
    };

    private static InputStream dorado(String nombre) {
        return DtoJsonTest.class.getResourceAsStream("/dorados/" + nombre);
    }
}
