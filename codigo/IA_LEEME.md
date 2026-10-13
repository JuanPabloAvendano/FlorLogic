REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 13-oct-2026, a pedido de Juan
Estado de revisión: SIN REVISAR
Manda por encima: `docs/03-arquitectura/flujos/transacciones/IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) · `ADR.xlsx`

# Código de FlorLogic — esqueleto

Armazón vacío para TX-1 (sincronización) y TX-2 (respaldo con observabilidad). **No hay lógica de
ninguna transacción**: sus filas TX siguen `POR ACEPTAR`. Lo que sí está aceptado (13-oct-2026):
TEC-01..TEC-30, P-T01..P-T07, P-S01, P-S02, P-S03 y P-N01.

## Cómo se usa

```
./mvnw verify                                   # compila todo y corre la prueba de límites de Modulith
docker compose -f despliegue/compose.yaml up -d # PostgreSQL 16 de desarrollo
java -jar backend-finca/target/backend-finca-0.1.0-SNAPSHOT.jar
curl localhost:8080/actuator/health
```

Solo hace falta un JDK 21 o superior; `mvnw` baja Maven 3.9.16.

## Qué hay

| Módulo | Qué es | Tecnología |
|---|---|---|
| `contrato/` | DTO, JSON Schema y huella canónica. Vacío hasta aceptar TX-1.K | Jackson 3 |
| `backend-finca/` | Monolito modular: los 15 módulos de §3 de `IA_TECNOLOGIAS`, cada uno con su `package-info` | Spring Boot 4.1.1 · Modulith 2.1.1 · JDBC · Flyway · PostgreSQL |
| `servicios-en-linea/` | Recepción de respaldos, telemetría y panel (TX-2) | Spring Boot 4.1.1 · puerto 8090 |
| `simulador/` | El celular simulado, sin órdenes todavía | picocli · SQLite |
| `pruebas-extremo-a-extremo/` | Vacío; aquí van las pruebas del §10 de cada TX | Testcontainers |
| `despliegue/` | `compose.yaml` del nodo, hoy solo con la base | Docker Compose |

`ModulosTest` falla si un módulo toca la parte interna de otro (TEC-05).

## Lo que falta para escribir la primera línea de TX-1

1. Aceptar TX-1, TX-1.C, TX-1.K y TX-1.T.
2. P-S04..P-S10 (jornada, nombres del contrato, canal por archivo, permiso retirado, tablero, umbrales).
3. Con TX-1.K aceptado, lo primero que se programa es `contrato/` y la tarjeta ES-S01.

TX-2 además necesita P-R01..P-R10; P-R10 (proveedor de nube) bloquea la subida real.
