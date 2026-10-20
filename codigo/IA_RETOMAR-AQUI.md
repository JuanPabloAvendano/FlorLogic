REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 19-oct-2026, a pedido de Juan, al cerrar la sesión
Estado de revisión: SIN REVISAR
Manda por encima: `contrato/ESPEC-contrato-ingesta.md` · `IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) · la palabra de Juan

# Retomar aquí — TX-1 después del canal

Para seguir con Claude basta decir: **«lee codigo/IA_RETOMAR-AQUI.md y sigamos»**.

## Estado en una línea
El canal de TX-1 funciona de punta a punta (simulador → backend-finca → PostgreSQL → respuesta). La skill
`detallar-para-codificar` trabaja ahora con el **camino mínimo funcional primero** (D-17).

## Comprobar que todo sigue bien (2 minutos, necesita Docker)
```
cd ~/Work/florlogic-codigo/codigo
./mvnw verify                 # 5 módulos en verde: contrato 24, backend 8, servicios 2, simulador 12
scripts/IA_probar-canal.sh    # «2 nuevas», reenvío «2 ya estaban», JSON roto «Rechazada», «Canal OK.»
```

## Lo que se decidió el 19-oct-2026 (Juan)
| Qué | Dónde quedó |
|---|---|
| TX-1.K, P-S04 (jornada = día natural de la finca, zona configurable) y P-S05 (`/ingesta/v1/…/particiones`) `ACEPTADO` | LEEME de transacciones |
| TX-1 `AJUSTAR`: aceptado solo el camino mínimo (el canal) | LEEME de transacciones |
| A-57: ninguna pieza del contrato lanza; lo imprevisto es un valor con nombre | ESPEC §2 |
| A-9: dos lectores, estricto (nodo de finca) y tolerante (celular) | ESPEC §2 |
| A-58: el nodo de finca validará y leerá por piezas (cuando haya rechazo por anotación) | ESPEC §2 |
| A-37: sin `module-info` (con Java 27 obligaba a forzar ASM) | ESPEC §2 |
| Paquetes de `contrato` por tipo técnico; la lectura en `contrato.lectura` | ESPEC §5 |
| Segmento 1 (forma de los mensajes) **ACEPTADO forzado** | ESPEC §8, §11 |
| Segmento 2 (el canal) escrito y en verde; **falta su revisión** | ESPEC §8, §9 |
| Skill: D-15 (consultar antes de escribir), D-16 (diagrama draw.io primero), D-17 (camino mínimo funcional) | `.claude/skills/detallar-para-codificar/DECISIONES.md` |

## Qué leer al volver, en orden
1. Este archivo.
2. `contrato/ESPEC-contrato-ingesta.md`: la Ficha y §8 (plan). No hace falta leer las HISTORIAS.
3. Los diagramas del canal: `contrato/diagramas/IA_segmento-2.drawio` (o los PNG al lado).
4. La skill la carga Claude solo.

## Siguiente, en orden
1. **Revisión corta del Segmento 2** (etapa 5) con el filtro del camino mínimo: solo lo que rompa el canal
   reabre el segmento; lo demás va a «después».
2. **Elegir qué entra después** (Juan decide el orden; cada uno es un segmento pequeño, diagrama primero):
   - la sesión `{id}`: abrir sesión (`POST /ingesta/v1/sesiones`) y verificar que exista;
   - la credencial (JWS, TEC-13) en `Authorization`;
   - la huella y la anotación corrupta (mismo id, otro contenido);
   - que el simulador arme la partición desde su almacén de salida (hoy manda un archivo);
   - el rechazo por anotación y los motivos (A-58, esquema JSON, segmentos 3 a 7 del contrato).
3. Lo de fuera del código: Juan escribe como ADR la decisión del 8-oct (Java, Spring Boot, Maven).

## Herramientas que quedaron listas
- **Biblioteca**: MCP `biblioteca` (61 libros, buscar con cita «Título · p. N»). En una sesión nueva carga solo;
  si no aparece, `/mcp`. Detalle en `~/Work/biblioteca/LEEME.md` (fuera de git).
- **draw.io** instalado; el generador está en `.claude/skills/detallar-para-codificar/herramientas/drawio.py`.
- **Eclipse**: el workspace `~/eclipse-workspace` ya tiene los proyectos; tras un cambio grande, F5 o
  *Maven › Update Project*.

## Reglas que no hay que olvidar
- Camino mínimo primero: «¿sin esto deja de funcionar el canal?». Si no, regla por defecto o «después».
- Cualquier fallo de forma en el POST es `422` «rechazado», sin motivo, hasta que Juan pida más.
- Commits y empujes: solo cuando Juan lo pide en la sesión (PR-05), nunca a `codigo-testing`, `codigo-listo` ni `main`.
- Ninguna dependencia nueva sin fila en `IA_TECNOLOGIAS-vigentes.md`.
