package co.florlogic.finca.ingesta.infraestructura.output.serviciosexternos;

import org.springframework.stereotype.Component;

import co.florlogic.contrato.interno.HuellaCanonica;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.CalculadorDeHuella;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * La huella con la forma canónica del contrato (A-86, A-106). Es el único adaptador de salida que conoce el contrato y
 * Jackson ({@code CapasTest}, COH-201): relee el JSON equivalente que guardó el ensamblador del controlador.
 */
@Component
class CalculadorDeHuellaAdapter implements CalculadorDeHuella {

    private static final ObjectMapper JSON = JsonMapper.builder().build();

    @Override
    public String de(String contenidoJson) {
        return HuellaCanonica.de(JSON.readTree(contenidoJson));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component: Spring crea una instancia y la entrega a quien pida el puerto CalculadorDeHuella.
 * - JsonMapper.builder().build() / JSON.readTree(contenidoJson) (Jackson 3, paquete tools.jackson): vuelve a leer
 *   el texto como árbol JSON para que HuellaCanonica lo recorra; en Jackson 3 un JSON roto lanza JacksonException,
 *   que no hay que declarar.
 */
