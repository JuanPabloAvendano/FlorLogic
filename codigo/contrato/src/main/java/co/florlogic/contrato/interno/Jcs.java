package co.florlogic.contrato.interno;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tools.jackson.databind.JsonNode;

/**
 * Forma canónica JSON de RFC 8785 (JCS) sobre el árbol de Jackson 3 (TEC-10; ESPEC-dominio-ingesta A-86): claves en
 * orden de unidades UTF-16, sin espacios, cadenas escapadas como {@code JSON.stringify} y todo número pasado a
 * {@code double} y escrito con el algoritmo de ECMAScript. Así dos JSON con el mismo contenido dan los mismos bytes.
 *
 * <p>Riesgo anotado: un entero mayor que 2⁵³ pierde precisión (igual que en JavaScript, que es lo que hace la PWA).
 */
public final class Jcs {

    private Jcs() {
    }

    public static String canonica(JsonNode nodo) {
        StringBuilder salida = new StringBuilder();
        escribir(nodo, salida);
        return salida.toString();
    }

    private static void escribir(JsonNode nodo, StringBuilder salida) {
        if (nodo == null || nodo.isNull() || nodo.isMissingNode()) {
            salida.append("null");
        } else if (nodo.isObject()) {
            salida.append('{');
            // String.compareTo compara unidades UTF-16, que es el orden que pide el RFC (secc. 3.2.3).
            List<String> nombres = new ArrayList<>(nodo.propertyNames());
            Collections.sort(nombres);
            boolean primero = true;
            for (String nombre : nombres) {
                if (!primero) {
                    salida.append(',');
                }
                primero = false;
                cadena(nombre, salida);
                salida.append(':');
                escribir(nodo.get(nombre), salida);
            }
            salida.append('}');
        } else if (nodo.isArray()) {
            salida.append('[');
            for (int i = 0; i < nodo.size(); i++) {
                if (i > 0) {
                    salida.append(',');
                }
                escribir(nodo.get(i), salida);
            }
            salida.append(']');
        } else if (nodo.isString()) {
            cadena(nodo.asString(), salida);
        } else if (nodo.isNumber()) {
            salida.append(numero(nodo.doubleValue()));
        } else if (nodo.isBoolean()) {
            salida.append(nodo.asBoolean() ? "true" : "false");
        } else {
            throw new IllegalArgumentException("jcs.tipo_no_json: " + nodo.getNodeType());
        }
    }

    /** Number::toString de ECMAScript (ECMA-262 secc. 6.1.6.1.20) sobre los dígitos más cortos de Java 19+. */
    static String numero(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("jcs.numero_no_json: " + valor);
        }
        if (valor == 0) {
            return "0";   // también -0
        }
        String signo = valor < 0 ? "-" : "";
        BigDecimal decimal = masCorto(Math.abs(valor));
        String digitos = decimal.unscaledValue().toString();
        int k = digitos.length();
        int n = k - decimal.scale();   // valor = 0.digitos × 10^n
        String texto;
        if (k <= n && n <= 21) {
            texto = digitos + "0".repeat(n - k);
        } else if (0 < n && n <= 21) {
            texto = digitos.substring(0, n) + "." + digitos.substring(n);
        } else if (-6 < n && n <= 0) {
            texto = "0." + "0".repeat(-n) + digitos;
        } else {
            int e = n - 1;
            String mantisa = k == 1 ? digitos : digitos.charAt(0) + "." + digitos.substring(1);
            texto = mantisa + "e" + (e < 0 ? "-" : "+") + Math.abs(e);
        }
        return signo + texto;
    }

    /**
     * Los dígitos más cortos que vuelven al mismo {@code double}. Java 19+ da los más cortos, salvo que nunca baja de dos
     * dígitos ({@code Double.toString(Double.MIN_VALUE)} es {@code 4.9E-324}); ECMAScript sí usa uno cuando alcanza
     * ({@code 5e-324}), y entre varios elige el más cercano.
     */
    private static BigDecimal masCorto(double valor) {
        BigDecimal deJava = new BigDecimal(Double.toString(valor)).stripTrailingZeros();
        if (deJava.precision() != 2) {
            return deJava;
        }
        BigDecimal exacto = new BigDecimal(valor);
        BigDecimal mejor = null;
        for (RoundingMode modo : new RoundingMode[] {RoundingMode.FLOOR, RoundingMode.CEILING}) {
            BigDecimal candidato = deJava.round(new MathContext(1, modo));
            if (Double.parseDouble(candidato.toString()) == valor
                    && (mejor == null || candidato.subtract(exacto).abs().compareTo(mejor.subtract(exacto).abs()) < 0)) {
                mejor = candidato;
            }
        }
        return mejor == null ? deJava : mejor.stripTrailingZeros();
    }

    /** Como JSON.stringify: solo se escapan las comillas, la barra y los de control; lo demás va tal cual. */
    private static void cadena(String texto, StringBuilder salida) {
        salida.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"' -> salida.append("\\\"");
                case '\\' -> salida.append("\\\\");
                case '\b' -> salida.append("\\b");
                case '\f' -> salida.append("\\f");
                case '\n' -> salida.append("\\n");
                case '\r' -> salida.append("\\r");
                case '\t' -> salida.append("\\t");
                default -> {
                    if (c < 0x20) {
                        salida.append(String.format("\\u%04x", (int) c));
                    } else {
                        salida.append(c);
                    }
                }
            }
        }
        salida.append('"');
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - StringBuilder: texto que se va armando con append sin crear una cadena nueva en cada paso.
 * - Collections.sort(nombres): ordena con String.compareTo, que compara unidades UTF-16 (lo que pide RFC 8785).
 * - isMissingNode(): Jackson devuelve un MissingNode (no null) cuando se pide algo que no existe; aquí se trata
 *   igual que null.
 * - isString() / asString(), isNumber() / doubleValue(), isBoolean() / asBoolean(): preguntas y lecturas del
 *   árbol de Jackson 3.
 * - getNodeType(): el tipo del nodo, solo para el mensaje de error.
 * - Double.isNaN / isInfinite: JSON no admite esos valores; se rechazan.
 * - BigDecimal: decimal exacto; unscaledValue() son los dígitos sin punto, scale() cuántos van tras el punto,
 *   precision() cuántos dígitos tiene, stripTrailingZeros() quita ceros a la derecha.
 * - MathContext(1, modo) con RoundingMode.FLOOR / CEILING: redondea a un solo dígito hacia abajo o hacia arriba;
 *   así masCorto prueba si un dígito basta para volver al mismo double.
 * - "0".repeat(n): repite el texto n veces.
 * - switch (c) { case '"' -> …; default -> { … } }: switch con flechas; cada caso corre solo lo suyo, sin break.
 * - String.format con %04x: el número en hexadecimal, cuatro cifras con ceros a la izquierda; es el escape de
 *   los caracteres de control (barra-u seguido de cuatro cifras).
 * - 0x20: el número 32 en hexadecimal; por debajo están los caracteres de control.
 */
