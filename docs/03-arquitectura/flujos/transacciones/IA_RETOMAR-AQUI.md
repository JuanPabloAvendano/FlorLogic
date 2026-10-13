REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 13-oct-2026, a pedido de Juan
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-LEEME-transacciones.md` (esta carpeta) · `ADR.xlsx` · `DRIVERS_ARQUITECTONICOS.md`

# Retomar aquí — preparación para programar TX-1 y TX-2

Punto de reentrada de la sesión del 13-oct-2026. Para seguir con Claude, basta decir:
**«lee docs/03-arquitectura/flujos/transacciones/IA_RETOMAR-AQUI.md y sigamos»**.

## Dónde quedó cada cosa

| Qué | Dónde | Estado |
|---|---|---|
| Tabla de aceptación con las respuestas de Juan | `IA_00-LEEME-transacciones.md` (esta carpeta) | Editada, **sin commit** |
| Línea de la sesión en la bitácora | `docs/03-arquitectura/spikes/IA_BITACORA.md` (última fila) | Añadida, **sin commit** |
| Esqueleto de código | `~/Work/florlogic-codigo/codigo/` — worktree de la rama `codigo-desarrollo` | Creado, **sin commit** |
| Cómo usar el esqueleto | `~/Work/florlogic-codigo/codigo/IA_LEEME.md` | — |
| Presentación para el revisor (en línea, privada) | https://claude.ai/artifact/AFpiky84LbUJYdWBqUrYgs | Sin compartir todavía |
| La misma presentación, sin conexión | `IA_PREPARACION-mazo.html` (esta carpeta; se abre en el navegador) | Copia local |
| Fuente de la presentación | `IA_PREPARACION-mazo/` (`deck.json` + una lámina por archivo) | Copia local |

## Lo decidido el 13-oct-2026 (ACEPTADO)

- **P-S01** (a): la captura incompleta entra y queda como trabajo del gerente (la del 15-sep).
- **P-S02** (a): `RF-022`, gana la más reciente, sin mediación, con aviso al que perdió.
- **P-S03** (a): el celular retiene hasta que la anotación está en un respaldo que salió del nodo.
- **P-N01** (a): credencial de 24 h más 7 días de gracia que solo permiten capturar.
- **P-T01..P-T07** y **TEC-01..TEC-30**: como se propusieron. Código en este repo, `codigo/`, rama `codigo-desarrollo`.

## Lo que sigue POR ACEPTAR

- Las filas TX-1, TX-1.C, TX-1.K, TX-1.T, TX-2, TX-2.O. Juan quiere revisarlas antes.
- P-S04..P-S10, P-R01..P-R10, P-N02..P-N06, y las filas ES, OC y UC.

## Próximos pasos, en orden

1. Revisar con la presentación y, si está bien, hacer los dos commits (los hace Juan, PR-05):
   - `~/Work/florlogic`, rama `documentacion-desarrollo`: el LEEME, la bitácora, este archivo y el mazo.
   - `~/Work/florlogic-codigo`, rama `codigo-desarrollo`: la carpeta `codigo/`.
2. Aceptar TX-1, TX-1.C, TX-1.K y TX-1.T, y contestar P-S04..P-S10.
3. Primer código: `contrato/` y la tarjeta ES-S01.
4. TX-2: P-R01..P-R10; P-R10 (proveedor de nube) bloquea la subida real.
5. Fuera del código: Juan escribe como ADR la decisión del 8-oct (Java, Spring Boot, Maven).

## Comprobar que todo sigue bien al volver

```
cd ~/Work/florlogic-codigo/codigo
./mvnw verify                                   # BUILD SUCCESS, ModulosTest 1/1
docker compose -f despliegue/compose.yaml up -d --wait
java -jar backend-finca/target/backend-finca-0.1.0-SNAPSHOT.jar --server.port=18080
curl localhost:18080/actuator/health            # {"status":"UP"}
docker compose -f despliegue/compose.yaml down -v
```

El 8080 está ocupado en el equipo por el proyecto glubia; por eso 18080. El JDK instalado es el 26 y se
compila para 21.

## Si se borra el worktree

`git worktree list` lo muestra. Si la carpeta `~/Work/florlogic-codigo` desapareció **antes del commit**,
el esqueleto se pierde. Para rehacer solo el worktree: `git worktree prune` y luego
`git worktree add ../florlogic-codigo codigo-desarrollo`.
