REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 22-oct-2026, a pedido de Juan (barra de progreso constante por proyecto)
Estado de revisión: SIN REVISAR · los pesos son inferidos, confirmar
Manda por encima: `IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) · `.claude/contexto/IA_PRIORIDAD-componentes-TX1.md` · las ESPEC de cada módulo · la palabra de Juan

# Progreso · FlorLogic
Producto final: FlorLogic en producción en una finca: lo anotado en la cama llega al nodo sin perderse, queda clasificado, el gerente lo ve, se respalda cifrado y se observa; con app de captura real y contingencia sin red.
Tarea actual: T-1.3

Formato y reglas: `~/.claude/skills/progreso/SKILL.md`. Cálculo: `progreso --revisar`.
Las tareas de TX-1 siguen el orden de `IA_PRIORIDAD-componentes-TX1.md` §1.

## H-0 · Arquitectura, drivers y aceptación de las transacciones · peso 8
- [x] T-0.1 · Drivers arquitectónicos y ADR · peso 3
- [x] T-0.2 · Dos transacciones definidas (TX-1, TX-2) y su mazo · peso 2
- [ ] T-0.3 · Aceptación completa de TX-1, TX-2, ES y UC en IA_00-LEEME · peso 3
  - [x] TX-1.K contrato aceptado (19-oct)
  - [x] P-S01..P-S05 aceptadas
  - [ ] TX-1 completa (hoy AJUSTAR: solo camino mínimo)
  - [ ] TX-1.C, TX-1.T
  - [ ] TX-2 y TX-2.O
  - [ ] ES-S, ES-R, ES-N, OC
  - [ ] UC-S, UC-R, UC-C

## H-1 · TX-1 nivel 0: el dato llega, se clasifica y el gerente lo ve · peso 14
- [x] T-1.1 · Canal de ingesta de punta a punta (65/65 con Docker) · peso 2
- [x] T-1.2 · Contrato de ingesta (ESPEC aceptada) · peso 1
- [ ] T-1.3 · Dominio de ingesta · Segmento 1 (valores y avance) · peso 2
  - [x] Tamaño
  - [x] Consulta
  - [x] Preguntas (88 %)
  - [x] Generación (104/104 con Docker)
  - [ ] Revisión: explicación de Juan (≈25 %, hace falta ≥80 %)
  - [ ] Verificación
  - [ ] Aceptación
- [ ] T-1.4 · Dominio de ingesta · avance conectado de punta a punta (segmento 1b) · peso 2
- [ ] T-1.5 · Dominio de ingesta · Segmentos 2 y 3 (Anotacion, traslado, huella K-115) · peso 2
- [ ] T-1.6 · Módulo `configuracion`: catálogo y versiones · peso 2
- [ ] T-1.7 · Módulo `reglas`: motor del servidor · peso 2
- [ ] T-1.8 · Módulo `ciclo`: ciclo de producción · peso 2
- [ ] T-1.9 · Módulo `consulta`: esperado, recibido y faltante · peso 2

## H-2 · TX-1 nivel 1: confianza · peso 8
- [ ] T-2.1 · `identidad` y `llaves` · peso 3
- [ ] T-2.2 · `dispositivos` · peso 1
- [ ] T-2.3 · `bitacora` de solo inserción · peso 2
- [ ] T-2.4 · Gateway Nginx + TLS con la CA de la finca · peso 2

## H-3 · TX-1 nivel 2-3: operación sostenida · peso 5
- [ ] T-3.1 · `notificacion` (bloqueada: decisión de red de Juan) · peso 2
- [ ] T-3.2 · `trabajos` (db-scheduler) · peso 1
- [ ] T-3.3 · WAF ModSecurity + CRS · peso 1
- [ ] T-3.4 · Caché y `proyeccion` · peso 1

## H-4 · TX-2: respaldo cifrado con observabilidad · peso 12
- [ ] T-4.1 · Detallar TX-2 (ESPEC) · peso 2
- [ ] T-4.2 · `respaldo` (restic, MinIO/nube) · peso 4
- [ ] T-4.3 · Plano de observabilidad (Prometheus, Loki, Grafana) · peso 4
- [ ] T-4.4 · Prueba de restauración programada · peso 2

## H-5 · App de captura real (celular) · peso 18
- [x] T-5.1 · Demo desechable PoC-0 · peso 1
- [ ] T-5.2 · Simulador del celular para TX-1 · peso 2
- [ ] T-5.3 · PWA de captura sin red (segmento propio) · peso 10
- [ ] T-5.4 · Sincronización real contra el nodo · peso 5

## H-6 · Contingencia: nodo dañado sin red (ES-N) · peso 7
- [ ] T-6.1 · Opciones OC-1..OC-9 decididas · peso 2
- [ ] T-6.2 · Sistema completo en contenedor en el celular del gerente · peso 5

## H-7 · Resto del producto más allá de TX-1 y TX-2 (por detallar) · peso 18
- [ ] T-7.1 · Transacciones y módulos aún sin definir · peso 1

## H-8 · Puesta en producción en una finca · peso 10
- [ ] T-8.1 · Despliegue del nodo y servicios en línea · peso 4
- [ ] T-8.2 · Piloto con usuarios reales · peso 4
- [ ] T-8.3 · Manual y soporte · peso 2
