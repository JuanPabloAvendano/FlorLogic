REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 por el orquestador (iteración 2)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `codigo/despliegue/compose.yaml` · la palabra de Juan

# 02 · Maven, Java y pruebas

Todo desde `codigo/`. Con Docker encendido, las pruebas de Testcontainers corren de verdad contra PostgreSQL y el
nodo; sin Docker salen `Skipped` y la construcción en verde **no prueba nada contra PostgreSQL**.

### Versión de Java y de Maven
`java -version` · `./mvnw -v` · `mise current`
- Partes: `mise current` dice qué versión fijó `mise.toml` (también lista las herramientas globales: claude, node…)
- Espera: `OpenJDK 27 … Temurin-27+35` · `Apache Maven 3.9.16 … Java version: 27` · `java temurin-27.0.0+35`
- Si falla: otra versión de Java = la terminal no cargó `mise`: `mise install` y abrir otra terminal
- Riesgo: ninguno · Necesidad: ÚTIL (primera pregunta de «línea base técnica»)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 1-3)

### Compilar los cinco módulos y correr todas las pruebas
`env -u FLORLOGIC_CA_RAIZ ./mvnw verify`
- Partes: `env -u FLORLOGIC_CA_RAIZ` corre la orden **sin** esa variable (sin borrarla de la terminal) · `./mvnw` el Maven Wrapper (baja Maven 3.9.16 la primera vez) · `verify` compila, empaqueta y corre pruebas unitarias e integración de `contrato`, `backend-finca`, `servicios-en-linea`, `simulador` y `pruebas-extremo-a-extremo`
- Por qué el `env -u`: si `FLORLOGIC_CA_RAIZ` está exportada y apunta a un archivo que no existe (p. ej. antes de la ceremonia), `SimuladorAplicacionTest` falla con `NoSuchFileException: …/raiz.crt`. Si la variable no está puesta, basta `./mvnw verify`
- Espera: `BUILD SUCCESS` en ~3,5 min con Docker (medido: 3 min 05 s y 3 min 27 s); 194 pruebas (24 + 134 + 2 + 24 + 10), 0 fallos, 0 saltadas
- Si falla: el nombre de la prueba sale en el resumen; el detalle en `<módulo>/target/surefire-reports/<Clase>.txt` (entrada de abajo). Con Eclipse abierto, nunca `clean` (deja `contrato` sin resolver)
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · `./mvnw verify` falló en `simulador` por la variable (fila 8); `env -u FLORLOGIC_CA_RAIZ ./mvnw verify` funcionó: 194 pruebas en 3:05 min (fila 9) · 2-nov-2026 · iteración 2 · funcionó con la variable apuntando a un archivo inexistente: 194 pruebas, 0 saltadas, 3 min 27 s; la trampa se reprodujo sin `env -u` (`NoSuchFileException`, 2 errores en `SimuladorAplicacionTest`) y se corrigió con él (filas 5, 16, 17)

### Un solo módulo con lo que necesita
`./mvnw -pl backend-finca -am verify`
- Partes: `-pl` (lista de proyectos) el módulo · `-am` (también construye) los módulos de los que depende, aquí `contrato`. `contrato` no depende de nadie: `./mvnw -q -pl contrato verify`
- Espera: contrato 24 + backend 134 pruebas, `BUILD SUCCESS` en ~1 min. Con `-q` no imprime nada si sale bien
- Riesgo: ninguno · Necesidad: ÚTIL (más rápido que todo)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 11, 12)

### Una sola clase de prueba o un solo método
`./mvnw -pl backend-finca -am -Dtest=SesionDeSincronizacionTest -Dsurefire.failIfNoSpecifiedTests=false verify`
- Partes: `-Dtest=<Clase>` solo esa clase; un método: `-Dtest='SesionDeSincronizacionTest#nombreDelMetodo'` · `-Dsurefire.failIfNoSpecifiedTests=false` evita que `contrato` (que también se construye por `-am`) falle por no tener esa clase
- Espera: `Tests run: 5, Failures: 0` · `BUILD SUCCESS`
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 10)

### Las pruebas de arquitectura (capas y módulos)
`./mvnw -pl backend-finca -am -Dtest='CapasTest,ModulosTest' -Dsurefire.failIfNoSpecifiedTests=false verify`
- Partes: `CapasTest` (ArchUnit: el dominio solo depende de Java, los servicios no ven la infraestructura…) · `ModulosTest` (Spring Modulith: un módulo solo usa la API pública de otro)
- Espera: `Tests run: 9, Failures: 0` · `BUILD SUCCESS`. Con una regla rota (hoja 09): `CapasTest.el_dominio_solo_depende_de_java_y_de_otro_dominio` en rojo con la clase culpable
- Riesgo: ninguno · Necesidad: ESENCIAL para la demo de Clean Architecture (área 1.4 de la guía)
- Tiempo: ~6-7 s
- Probado: 2-nov-2026 · iteración 1 · funcionó en rojo y en verde (filas 99, 101) · 2-nov-2026 · iteración 2 · funcionó: `Tests run: 9` en 6 s, rojo y verde (filas 125-129)

### Dónde quedan los resultados de las pruebas
`ls backend-finca/target/surefire-reports/` · `less backend-finca/target/surefire-reports/<Clase>.txt`
- Partes: un `.txt` (resumen y traza) y un `.xml` por clase
- Espera: `co.florlogic.finca.ArranqueBackendFincaTest.txt`, `…CapasTest.txt`, …
- Riesgo: ninguno · Necesidad: ÚTIL (ahí sale el `Caused by` real de un fallo)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 14); fue donde se halló la causa de la fila 8

### Árbol de dependencias
`./mvnw -pl backend-finca dependency:tree`
- Partes: `dependency:tree` imprime qué librerías trae el módulo y por quién. **Sin `-q`**: el árbol se escribe en nivel INFO y `-q` lo oculta
- Espera: `co.florlogic:backend-finca` → `co.florlogic:contrato:compile` …, `BUILD SUCCESS`
- Riesgo: ninguno · Necesidad: ÚTIL (pregunta de «de dónde sale esta librería», y antes de revisar CVE)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 15); con `-q` salió 0 sin imprimir nada (fila 13) · 2-nov-2026 · iteración 2 · funcionó sin `-q`: 153 líneas en 2,2 s (fila 18)

### Construir sin pruebas (rápido)
`./mvnw -q -DskipTests package`
- Partes: `-q` silencioso · `-DskipTests` compila las pruebas pero no las corre · `package` deja los jar en `*/target/`
- Riesgo: ninguno · Necesidad: RARA · Motivo de necesidad: solo para tener los jar rápido; para el simulador basta `-pl simulador -am` (hoja 06), y la imagen del backend compila sola en su `Dockerfile`
- Probado: sin probar en todo el repo; la variante del simulador funcionó (fila 39)
