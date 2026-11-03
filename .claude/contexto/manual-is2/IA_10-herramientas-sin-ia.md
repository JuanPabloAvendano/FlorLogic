REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 (iteración 2) y el 3-nov-2026 (iteración 3, final) por el orquestador
Estado de revisión: SIN REVISAR
Manda por encima: `README.md` de `documentacion-desarrollo` (sus diagramas mandan) · `codigo/IA_LEEME.md` · la palabra de Juan

# 10 · Herramientas y fuentes sin IA

## Cuando no recuerdo una bandera

El probador de la iteración 1 encontró casi todo lo que le faltaba al manual leyendo la ayuda. Lo que le sirvió:

| Necesitaba… | Lo encontró en | Qué halló |
|---|---|---|
| esperar a que un servicio esté sano | `docker compose up --help` | `--wait`, `--force-recreate`, `--no-deps` |
| desmontar un solo servicio | `docker compose rm --help` | `-s` (parar antes), `-f` (sin preguntar) |
| listar servicios sin imprimir secretos | `docker compose config --help` | `--services`, `-q` |
| proyectos detenidos | `docker compose ls --help` | `-a` |
| sacar un campo de un JSON de Docker | `docker network inspect --help` | `--format '{{…}}'` |
| ver toda la cadena TLS | `man openssl-s_client` | `-showcerts` |
| pasar la clave a psql sin teclearla | `man psql` | la variable `PGPASSWORD` |
| la causa real de una prueba roja | `<módulo>/target/surefire-reports/<Clase>.txt` | el `Caused by` |
| el cuerpo JSON de una petición | `contrato/src/main/java/co/florlogic/contrato/dto/*Dto.java` | los nombres de `@JsonProperty` |
| el nombre real de una columna | el `HINT` del propio error de psql | `Perhaps you meant … sesion_id` (iteración 2, fila 23) |
| dónde se monta un secreto | `despliegue/compose.yaml`, bloque `secrets:` de cada servicio | `pg-admin` en `base` y `openbao-init` (iteración 2, fila 112) |
| el puerto publicado de un servicio | `docker compose port --help` | `DC port <servicio> <puerto>` (salida críptica: hoja 01) |
| qué significa un `000` de curl | `man curl`, sección EXIT CODES | 7, 35, 56, 60 (hoja 04) |

- Riesgo: ninguno · Necesidad: ESENCIAL (es lo que permite improvisar ante el evaluador)
- Probado: 2-nov-2026 · iteración 1 · funcionó (las filas «descubierta» del registro: 4, 5, 9, 34, 44, 60, 84, 90, 119, 131, 135, 152) · 2-nov-2026 · iteración 2 · funcionó (filas 24, 40, 109, 112, 116)

## Herramientas y documentación

| Necesito… | Herramienta | Cómo | Probado |
|---|---|---|---|
| Ayuda de cualquier orden | `--help`, `man` | `docker compose up --help`, `man psql`, `man curl`, `./mvnw --help` | funcionó (arriba) |
| Ayuda de psql y SQL | psql | `\?`, `\h DELETE` | funcionó (filas 63, 68) |
| Leer o filtrar JSON | jq 1.8 (instalado) | detrás de una tubería, `jq .` ordena; `jq -c .` en una línea; `jq -r .campo` saca un campo sin comillas | 2-nov-2026 · iteración 2 · funcionó (filas 20-22, 25) |
| Ver contenedores de forma visual | lazydocker (instalado) | desde `codigo/`: `lazydocker -f despliegue/compose.yaml -p despliegue` (solo este proyecto; `-f` archivo de Compose, `-p` proyecto) · sin banderas muestra **todos** los contenedores de la máquina. Dentro: flechas para moverse entre paneles y contenedores; el panel de la derecha muestra los logs del elegido; `?` lista los atajos del panel (parar, reiniciar, borrar: cuidado con borrar), `q` sale. Los atajos exactos, sin comprobar: mirarlos con `?` | `--help`: 2-nov-2026 · iteración 2 · funcionó: banderas `-f`, `-p`, `-c`, `-d` (fila 133); la pantalla, sin probar: interactiva |
| Ver diagramas | draw.io (instalado) | `drawio <archivo>`; a PNG sin abrir: `drawio -x -f png -o X.png X.drawio` | sin probar por el probador |
| Docs oficiales de Compose | docs.docker.com | <https://docs.docker.com/reference/cli/docker/compose/> | — |
| Docs de PostgreSQL 16 | postgresql.org | <https://www.postgresql.org/docs/16/app-psql.html> | — |
| Docs de Maven Surefire (`-Dtest`) | maven.apache.org | <https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html> | — |
| Docs de OpenBao | openbao.org | <https://openbao.org/docs/commands/> | — |
| Docs de Nginx / ModSecurity / CRS | nginx.org, coreruleset.org | <https://nginx.org/en/docs/>, <https://coreruleset.org/docs/> | — |
| Docs de curl | curl.se | <https://curl.se/docs/manpage.html> | — |
| Libros y docs oficiales del stack | biblioteca local | `~/Work/biblioteca` (citar «Título · p. N») | — |

- Necesidad: ÚTIL (lazydocker: RARA · Motivo de necesidad: ver de un vistazo estado y logs si el público prefiere algo visual; todo lo hace `DC ps -a` y `DC logs`)
