# Corrige solo el gerente de producción · el operario edita lo no enviado o recaptura
Qué es: recreación, con el formato de `decisiones/`, de una decisión que Juan tomó al detallar el contrato de ingesta.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` (A-49) y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR-PoC-Spikes.xlsx` · `DRIVERS_ARQUITECTONICOS.md` · la palabra de Juan.

> **DECISIÓN DE JUAN · 16-oct-2026 (20:38 y 22:02 UTC, rondas 1e P4 y 1f P1)**
> Estado: **DECIDIDA · PENDIENTE DE ADR**
> Va al ADR: ______________
> Toca: `RF-016` · `RF-017` · `ESC-08` · `ESC-58` · `ADR-024` §1 · `ADR-027`

## 1 · Lo decidido
1. **Las correcciones sobre lo ya registrado las hace solo el gerente de producción**, en el tablero.
2. El operario **no corrige**: cambia en su celular lo que todavía no salió en ninguna partición, y solo viaja la versión final.
3. Lo que ya salió es inmutable. Juan: «Inmutable desde que sale, eso es otra captura nueva de la cama». La captura nueva lleva otro id y decide RF-022.
4. El único motivo obligatorio es el de una **desviación brusca** según las reglas.

## 2 · Los motivos
La trazabilidad se lleva contra el catálogo y los cambios bruscos, no contra la historia de las
ediciones locales (A-49 ⟦5⟧). Que lo enviado no cambie evita corrupciones falsas en la huella.

## 3 · Lo que choca
- `RF-017`: «solo el **administrador de la empresa** —el ingeniero de sistemas de la finca—» modifica lo sincronizado. ¿Son el mismo rol, o cambia el requisito?
- `RF-016`: «al capturador no se le exige motivo escrito ni autorización para corregir».
- `ESC-08`: la corrección en la cola local «conserva el valor original y el motivo».
