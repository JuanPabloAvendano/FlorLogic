REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 28-oct-2026, a pedido de Juan («todo lo que está en GitHub manda ahora, es humano; tráete las imágenes»)
Estado de revisión: SIN REVISAR
Manda por encima: `README.md` de la rama `documentacion-desarrollo` (commit `f8e5bb4`) y sus imágenes en `imagenes/`

# Imágenes de arquitectura del README de documentación (copia de lectura)

Son copias exactas de `origin/documentacion-desarrollo:imagenes/` al 28-oct-2026. **Mandan** (son humanas): son la
fuente de «Arquitectura y modelado» de la guía de IS II. Si cambian en GitHub, se vuelven a copiar; aquí no se editan.

| Archivo | README | Qué muestra |
|---|---|---|
| `C4-drawio-original - 01 - C4 - Nivel 2 - Contenedores.png` | §5 | Contenedores y personas |
| `C4-drawio-original - 06 - C4 - Despliegue.png` | §5 | Nodos N1 Android, N2 servidor de la finca, N3 oficina, N4 nube |
| `C4-drawio-original - 02 - C4 - Nivel 3 - Backend de la finca.png` | §6 | Componentes del backend, con la tecnología propuesta de cada uno |
| `C4-drawio-original - 03 - C4 - Nivel 3 - App de captura.png` | §6 | Componentes de la app de captura |
| `C4-drawio-original - 04 - C4 - Nivel 3 - Aplicación web del sistema.png` | §6 | Componentes de la web |
| `C4-drawio-original - 05 - C4 - Nivel 3 - Servicios en línea.png` | §6 | Componentes de la nube |
| `diagrama de paquetes - 01 - diagrama paquetes para ingesta.png` | §7 | El hexágono de `ingesta` y `despliegue` |
| `diagrama de secuencia - 01 - secuancia inicial.png` | §8 | Recibir una partición, camino feliz (20 pasos) |
| `diagrama de secuencia - 02 - secuancia errores.png` | §8 | Recibir una partición, fallos y respuestas |

Volver a copiar:
```
git fetch origin documentacion-desarrollo
git -c core.quotepath=off ls-tree -r --name-only origin/documentacion-desarrollo imagenes/ | while IFS= read -r f; do
  git show "origin/documentacion-desarrollo:$f" > ".claude/contexto/referencias/arquitectura-readme/$(basename "$f")"; done
```
