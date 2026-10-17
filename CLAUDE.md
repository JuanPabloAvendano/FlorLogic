# FlorLogic · rama de código — instrucciones para Claude

Rama `codigo-desarrollo`. Aquí vive el código de FlorLogic (sector floricultor) en `codigo/`: Java,
Spring Boot y Maven. La documentación de arquitectura vive en la rama `documentacion-desarrollo`.
Arquitecto y dueño de las decisiones: Juan Pablo. Todo el repo está en español.

## Antes de escribir código, lee en este orden

1. `codigo/IA_TECNOLOGIAS-vigentes.md` — **qué tecnología está aceptada, con qué versión, y cómo entra
   una nueva.** Si algo no está ahí como `ACEPTADO`, no se usa.
2. `codigo/IA_LEEME.md` — cómo se construye, qué módulos hay, qué pruebas existen.
3. La especificación de la tarea, que está en la otra rama. Se lee sin cambiar de rama:
   ```
   git fetch origin documentacion-desarrollo
   git show origin/documentacion-desarrollo:docs/03-arquitectura/flujos/transacciones/IA_00-LEEME-transacciones.md
   ```
   En esa carpeta: `IA_TX1-sincronizacion.md`, `IA_TX2-respaldo-y-observabilidad.md`,
   `IA_TECNOLOGIAS-y-estructura.md`, `IA_RETOMAR-AQUI.md`.

## Reglas que mandan

- **Solo se programa lo aceptado.** Una transacción se programa cuando su fila TX, sus preguntas P y
  las TEC que usa están `ACEPTADO` en `IA_00-LEEME-transacciones.md`. Hoy TX-1 y TX-2 siguen
  `POR ACEPTAR`: si la tarea lo exige, **para y pregunta**, no supongas.
- **Ninguna dependencia nueva sin fila en `IA_TECNOLOGIAS-vigentes.md`.** El procedimiento está ahí.
- **Commits y empujes los hace Juan (PR-05).** La IA solo hace commit o push cuando Juan lo pide de
  forma explícita en esa sesión, y nunca a `codigo-testing`, `codigo-listo` ni `main`.
- **Capas, dentro de cada módulo del backend:** `infraestructura` → `servicios` → `dominio`.
  `dominio` es Java puro: sin Spring, sin SQL, sin `contrato`. Un módulo solo usa la API pública
  (paquete raíz) de otro; `ModulosTest` lo verifica.
- **Dominio en español** (`Cama`, `Seccion`, `SesionCaptura`); lo técnico en el idioma del armazón (PR-01).
- **Ninguna llave ni secreto en el repo** (PR-06).
- Lo que genera la IA lleva prefijo `IA_` y la cabecera de cuatro líneas (qué es, quién, estado,
  qué manda por encima). Los estados posibles son `SIN REVISAR`, `REVISADO POR JUAN <fecha>`,
  `APLAZADO <fecha> · <motivo>` y `DESCARTADO <fecha> · <motivo>`.
- `ADR.xlsx` y los `.xlsx` de drivers **no están en git** (están en `.gitignore`). Si una tarea
  depende de un ADR que no aparece citado en los `.md`, pregunta.

## Construir y probar

```
cd codigo
./mvnw verify          # compila los cinco módulos y corre las pruebas
```

- **JDK: Temurin 27** (`mise.toml` en la raíz lo fija). El `pom.xml` compila con `release 27`.
- **Docker:** `ArranqueBackendFincaTest` y todo lo de Testcontainers **se salta sin Docker**, y en la
  nube normalmente no hay. Una construcción en verde sin Docker **no prueba nada contra PostgreSQL**:
  dilo en el resumen de la sesión, no lo des por probado.
- Terminado = la prueba automática de la tarjeta ES está en verde (PR-04). Sin prueba, no está terminado.

## Al cerrar una sesión

1. `./mvnw verify` en verde, o el fallo explicado.
2. Si cambió una versión o entró una tecnología: actualizar `codigo/IA_TECNOLOGIAS-vigentes.md`
   (tabla y registro de cambios).
3. Si cambió cómo se construye o qué módulos hay: actualizar `codigo/IA_LEEME.md`.
4. Resumen para Juan: qué se hizo, qué quedó sin probar y qué decisión le toca a él.
