REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 21-oct-2026, a pedido de Juan, al pausar la sesión del dominio de ingesta
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` (Ficha, §8, §10, §11) · la palabra de Juan

# LEER PRIMERO · URGENTE

> **Para el agente que abra la siguiente sesión:** antes de cualquier otra cosa, **muéstrale a Juan este archivo
> completo**, tal cual, y pregúntale por dónde sigue. No escribas código antes. Cuando Juan lo haya visto y los dos
> puntos de abajo estén cerrados, quita la línea que apunta aquí en `.claude/CLAUDE.md` y marca este archivo
> `DESCARTADO <fecha> · cumplido`.

Todo lo de esta sesión está en la copia visible `~/Work/florlogic-codigo` (rama `codigo-desarrollo`), **sin commit**
(PR-05). **Nunca se trabaja en un worktree** (pedido de Juan, 21-oct).

## 1 · Falta la explicación de Juan: sin ella el Segmento 1 no está aceptado

El Segmento 1 del dominio de ingesta (valores y `AvanceDelDispositivoAgregado`) está **verificado pero no aceptado**.
De los 7 pasos de la verificación (skill `detallar-para-codificar`, etapa 6) faltan el **6 y el 7**:

| Paso | Estado |
|---|---|
| 1 Observaciones | cumplidas: OBS-1-1…OBS-1-5 (la 5 decidida, ver §2), COH-140, COH-142…COH-147. COH-141 va al segmento de `Anotacion` |
| 2 Lista de 22 puntos | hecha; el punto 4 quedó en **No** (código anticipado, OBS-1-5) y el 22 pendiente |
| 3 Build | `./mvnw verify` 104/104 con Docker (21-oct) |
| 4 Coherencia | pasa, sin bloqueantes |
| 5 Completitud | 100 % sobre el camino mínimo |
| **6 Explicación** | **pendiente: ≈25 %, hace falta ≥80 %** |
| 7 Aceptación | espera el paso 6 |

Lo que Juan ya explicó bien (21-oct): la teoría del objeto de valor («se define con sus propias reglas de
existencia») y la del agregado («define las reglas de un conjunto»). Lo que falta, clase por clase, la práctica (qué
hace aquí) y la teoría (patrón y para qué sirve):

1. `ConsecutivoValor`
2. `AvanceDelDispositivoAgregado`: qué guarda (`confirmadoHasta`, `porEncima`), de quién (un solo celular) y qué
   hace `registrar`. Ojo: no «gobierna los objetos de `comun`»; solo usa `ConsecutivoValor` y `DispositivoId`.
3. `AnotacionId` y `DispositivoId`
4. `HuellaValor`
5. `CapturaValor`
6. Los enums `Presencia` (lo que entra al juicio), `Resultado`, `CodigoMarca`, `MotivoRechazo` (lo que sale). La
   tabla está en el `package-info` de `ingesta/dominio/comun`.
7. **Flujo (punto 22):** un celular manda 1, 2, 4 y después 3: ¿cuánto valen `confirmadoHasta` y `porEncima` tras
   cada uno?

Ayuda que pidió Juan y que sirve para explicar: el cuadro «lo que conocías → dónde está aquí» (entidad, repositorio,
DAO, fábrica, `record`) quedó en el chat del 21-oct; si lo necesita, rehazlo desde el diccionario («Objeto de
valor», «Raíz de agregado», «Record», «Sufijo de papel») y `codigo/IA_LEEME.md` «Nombres de las clases».

## 2 · Lo siguiente, decidido el 21-oct: conectar el avance de punta a punta

Juan notó que el Segmento 1 se escribió «a salvo para algo que todavía no existe»: **ninguna clase fuera de
`ingesta/dominio` lo usa**; `desde`, `version` y la copia ordenada de `porEncima` esperan un repositorio de avance
que no hay, y el canal sigue respondiendo como `confirmado_hasta` el **mayor consecutivo de la partición** (atajo
que rompe A-31). Decidió (OBS-1-5):

- **Segmento siguiente = el avance conectado en el nodo**, antes que `Anotacion` y sus reglas:
  - puerto de salida `AvancesPuertoSalida` (cargar por `DispositivoId`, guardar) en `servicios/puertos/salida`;
  - adaptador `AvancesAdaptadorJdbc` en `infraestructura/salida/persistencia`, que usa `desde` y `version`;
  - migración `V3__avance_dispositivo.sql`;
  - `RecibirParticionCasoUso` registra cada consecutivo en el avance y la respuesta lleva el `confirmado_hasta` real;
  - prueba contra PostgreSQL (Testcontainers; sin Docker se salta y **no prueba nada**: dilo).
- **Casos fijos** (pedido de Juan): particiones JSON estáticas, solo con el envoltorio de anotaciones (sin bloques ni
  camas), en `codigo/simulador/casos/`, y un `scripts/IA_probar-avance.sh` que las manda en orden y muestra el
  progreso: `1,2 → 2` · `4 → 2 (falta el 3)` · `3 → 4` · `4 otra vez → 4 (sin efecto)`.
- **La PWA va después**, en su propio segmento: un botón por caso en `~/Work/florlogic/app-captura` (PoC-0,
  desechable, rama `documentacion-desarrollo`). Pide CORS en el gateway de Jeronimo
  (`codigo/despliegue/gateway/default.conf.template`). El celular y la PWA no se detallan.

**Por confirmar con Juan antes del plan** (lo asumió Claude y Juan no lo corrigió, pero no lo eligió):
- **Un solo celular, con su id en la configuración del nodo**, mientras no existan las sesiones (A-52: el celular
  es el de la sesión; abrir sesiones es «parte 2»). Con dos celulares no sirve.
- Reglas por defecto del camino mínimo: consecutivo 0 → toda la partición `422` (A-70); dos particiones del mismo
  celular a la vez → `version` lo detecta, error genérico y el celular reintenta.

**Cómo se arranca** (skill `detallar-para-codificar`): esto sale del borde de `ESPEC-dominio-ingesta` (es caso de uso
y adaptador de persistencia: semillas `codigo/contrato/espec/IA_apartado-2026-10-18/semillas/IA_SEMILLA-caso-de-uso-ingreso.md`
y `IA_SEMILLA-adaptador-persistencia-ingreso.md`). Sin índice ≥85 % no hay código: una ronda corta solo con lo que
pide el camino mínimo, después el plan con su diagrama draw.io (D-16) y el árbol de archivos (D-20), y el «sí» de
Juan.

## Orden

1. Mostrarle este archivo a Juan.
2. Terminar la explicación del Segmento 1 (§1) y aceptarlo.
3. Confirmar los supuestos de §2 y detallar el avance conectado; luego plan, diagrama y código.
4. Después: la PWA; luego `Anotacion` y sus reglas (antes «Segmento 2») y el traslado de `AnotacionRecibida`.

## Pendiente de Juan, fuera de esto

- Hacer commit (PR-05): todo está local.
- Borrar el worktree viejo, si quiere: `git worktree remove --force .claude/worktrees/dominio-tx1 && git branch -D ia/dominio-tx1`.
- La fila TX-1 de `IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) sigue `AJUSTAR`.
