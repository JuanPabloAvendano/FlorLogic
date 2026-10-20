# Nada desconocido entra a la finca · el nodo de finca crea todo lo que llega al celular
Qué es: recreación, con el formato de `decisiones/`, de dos principios que Juan dijo al detallar el contrato de ingesta y que valen para todo el sistema.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` (A-32, A-43, A-50) y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR-PoC-Spikes.xlsx` · `DRIVERS_ARQUITECTONICOS.md` · la palabra de Juan.

> **DECISIÓN DE JUAN · 16-oct-2026 (20:43, 20:46, 22:10 y 22:52 UTC)**
> Estado: **DECIDIDA · PENDIENTE DE ADR**
> Va al ADR: ______________
> Toca: `ADR-014` · `ADR-015` · `ADR-016` · `ADR-029` · `ESC-07`

## 1 · Lo decidido
**A · Nada desconocido entra.** Juan: «el sistema se construye para tener datos definidos y
esperados»; un campo desconocido «es peligroso» porque no hay cómo controlar sus efectos. Un dato
que no está en un tipo ya establecido es fallo técnico.

**B · Lo que va hacia el celular es despliegue.** Juan: «Nada entra a la finca; si es hacia el
celular, va en despliegue y actualización». Cómo lee el celular una respuesta más nueva no es del
contrato.

**C · El nodo de finca crea todas las versiones.** Juan: «el nodo de finca crea cualquier cosa que
llega al celular»; «la configuración nace, se crea y se distribuye desde el backend de la finca».
Hay cuatro ejes de versión independientes: app/despliegue, motor, configuración (catálogo y reglas
en un solo paquete, ADR-029) y contrato. Un celular nunca tiene una versión que el nodo de finca no tenga.

## 2 · Consecuencias
- Una versión desconocida en operación es una **anomalía** que se alerta al administrador, no un caso normal.
- El nodo de finca se actualiza antes que los celulares.

## 3 · Agregado por el agente, confirmar
«Con `contrato-incompatible` la ventana de retención no corre» (A-50, C-62): Juan no lo dijo.
