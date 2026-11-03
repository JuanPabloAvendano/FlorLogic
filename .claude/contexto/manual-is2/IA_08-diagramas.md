REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 por el orquestador
Estado de revisión: SIN REVISAR
Manda por encima: `README.md` de `documentacion-desarrollo` (sus diagramas mandan) · `codigo/IA_LEEME.md` · la palabra de Juan

# 08 · Diagramas: cuál hay, qué muestra y cómo abrirlo sin IA

Abrir: `drawio <archivo>.drawio` (instalado) · exportar a PNG sin abrir:
`drawio -x -f png -o salida.png <archivo>.drawio` (`-p <n>` una página, `--all-pages` todas).
Rutas desde la raíz del repo `florlogic-codigo/`.

## Los que mandan (README de `documentacion-desarrollo`, copiados como imagen)

| Diagrama | Para qué área de la guía | Archivo |
|---|---|---|
| C4 nivel 2 · contenedores | 1.2 Arquitectura | `.claude/contexto/referencias/arquitectura-readme/C4-drawio-original - 01 - C4 - Nivel 2 - Contenedores.png` |
| C4 nivel 3 · backend de la finca | 1.2 Componentes | `… - 02 - C4 - Nivel 3 - Backend de la finca.png` |
| C4 nivel 3 · app de captura, web, servicios en línea | 1.2 | `… - 03`, `… - 04`, `… - 05` |
| C4 despliegue | 1.2, 1.7 | `… - 06 - C4 - Despliegue.png` |
| Paquetes de `ingesta` | 1.2, 1.4 Clean Architecture | `… diagrama de paquetes - 01 - diagrama paquetes para ingesta.png` (choque CH-7 de la guía IS II §9: dibujado el 28-oct, puede no coincidir con el reempaquetado hexagonal del 30-oct, «Segmento R») |
| Secuencia camino feliz y errores | 1.2 Secuencia | `… diagrama de secuencia - 01 …`, `… - 02 …` |
| C4 nivel 1 | 1.2 | **no existe** (FALTA en la guía) |

## Los de las ESPEC (IA, antecedente)

| Diagrama | Qué muestra | Archivo |
|---|---|---|
| Paquetes del repo | Módulos Maven y paquetes | `codigo/IA_DIAGRAMA-paquetes.drawio` |
| Nodo en Compose y orden de arranque | base → openbao → openbao-init → backend/gateway | `codigo/despliegue/espec/diagramas/IA_segmento-1-nodo.png`, `IA_segmento-1-orden.png` |
| Gateway: contexto, configuración, prueba | Nginx como única puerta | `codigo/despliegue/diagramas/IA_segmento-1.drawio` |
| WAF: contexto, configuración, prueba | ModSecurity + CRS | `codigo/despliegue/diagramas/IA_waf-segmento-1.drawio` |
| Hexágono de `ingesta` | Puertos, adaptadores, ensambladores | `codigo/backend-finca/espec/diagramas/IA_reempaquetado-hexagonal.drawio`, `IA_segmento-R2.drawio` |
| Dominio de `ingesta` | Agregados y valores | `codigo/backend-finca/espec/diagramas/IA_segmento-1.drawio`, `IA_segmento-1b.drawio` |
| Contrato: módulos, clases, recorrido | DTO y lector | `codigo/contrato/espec/diagramas/IA_segmento-1.drawio`, `IA_segmento-2.drawio` |
| Pirámide de pruebas del simulador | 1.6 Pruebas | `codigo/contrato/espec/diagramas/IA_segmento-2-verificacion.drawio` |

## Los de este manual

| Diagrama | Qué muestra | Para qué área de la guía | Archivo |
|---|---|---|---|
| Entidad-relación de hoy | Las tablas de V2…V4 y `flyway_schema_history`, sus enlaces lógicos (sin llaves foráneas) y la regla del CHECK; etiquetas separadas el 2-nov | 1.2 Modelado | `diagramas/IA_entidad-relacion.drawio` (+ `.png`) |
| Modelo de dominio de `ingesta` | Los paquetes `sesion`, `avance`, `recepcion` y `comun` de `ingesta/dominio`: las dos raíces de agregado (`SesionDeSincronizacion`, `AvanceDelDispositivoAgregado`), los valores, enumeraciones y resultados cerrados (sealed), y en gris los tipos preparados que aún no usa nadie. Se leyó del código el 2-nov | 1.2 Modelado · 1.4 Clean Architecture (el dominio es Java puro) | `diagramas/IA_modelo-dominio-ingesta.drawio` (+ `.png`) |
| Mapa de órdenes sobre el nodo | Qué orden toca qué contenedor, y por dónde pasa el tráfico (HTTPS → gateway → mTLS → backend → base; AppRole → vault) | 1.2 Despliegue · 1.7 | `diagramas/IA_mapa-ordenes-nodo.drawio` (+ `.png`) |

- Abrir: `drawio diagramas/<archivo>.drawio` (necesita pantalla) · ver sin draw.io: abrir el `.png` con cualquier visor
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: sin probar por el probador (los PNG los exportó el orquestador al dibujarlos, con `drawio -x -f png -o X.png X.drawio`; abrirlos necesita pantalla)

## Cómo generar diagramas sin IA

| Qué | Cómo |
|---|---|
| Entidad-relación desde la base | `pg_dump --schema-only` y draw.io › Organizar › Insertar › Avanzado › SQL (hoja 03) |
| Modelo de dominio desde el código | draw.io › Organizar › Insertar › UML / clases a mano, mirando `backend-finca/src/main/java/co/florlogic/finca/ingesta/dominio/` |
| Dependencias entre módulos | `ModulosTest` lo comprueba; Spring Modulith puede escribir la documentación de módulos (`Documenter`) si se habilita en una prueba |
| Topología del nodo | `DC config --services`, `DC ps -a` (puertos publicados) y `docker network inspect despliegue_default --format …` (hoja 01): servicios, quién publica puertos y la IP de cada uno |
