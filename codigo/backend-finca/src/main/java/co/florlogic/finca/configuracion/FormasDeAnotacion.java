package co.florlogic.finca.configuracion;

import java.util.Optional;

/**
 * API pública del catálogo de parámetros (ESPEC-dominio-ingesta A-88, A-95): la forma de cada tipo de anotación en
 * cada versión. Vacío si el tipo o la versión no existen; quien pregunta decide qué hacer (A-101: tipo desconocido).
 */
public interface FormasDeAnotacion {

    Optional<FormaDeAnotacion> de(String tipo, int version);
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Optional<FormaDeAnotacion>: una caja que trae la forma o viene vacía; obliga a quien pregunta a decidir qué hacer
 *   cuando el tipo o la versión no existen, en vez de recibir un null que se le pueda olvidar revisar.
 */
