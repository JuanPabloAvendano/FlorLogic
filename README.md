# FlorLogic

## Estrategia de ramas

Este repositorio usa las siguientes ramas:

- `main` — rama principal, contiene el código y la documentación estables/listos para producción.
- `documentacion-desarrollo` — documentación en curso, borradores y contenido en preparación.
- `documentacion-lista` — documentación revisada y lista para integrarse a `main`.
- `codigo-desarrollo` — desarrollo activo de nuevas funcionalidades.
- `codigo-testing` — código en pruebas/QA antes de pasar a producción.
- `codigo-listo` — código validado y listo para integrarse a `main`.

Flujo sugerido: el trabajo nuevo se hace en `codigo-desarrollo` o `documentacion-desarrollo`, pasa por `codigo-testing` (o revisión, en el caso de documentación) y de ahí a `codigo-listo` / `documentacion-lista`, para finalmente integrarse a `main`.

Ver [docs/README.md](docs/README.md) para la estructura de la documentación.
