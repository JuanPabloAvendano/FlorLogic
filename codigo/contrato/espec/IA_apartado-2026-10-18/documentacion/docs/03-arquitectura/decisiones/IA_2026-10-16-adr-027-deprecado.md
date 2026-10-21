# ADR-027 queda deprecado por mal conceptualizado
Qué es: recreación, con el formato de `decisiones/`, de una decisión que Juan tomó al detallar el contrato de ingesta.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` (A-49 ⟦6⟧, K-41) y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR-PoC-Spikes.xlsx` · `DRIVERS_ARQUITECTONICOS.md` · la palabra de Juan.

> **DECISIÓN DE JUAN · 16-oct-2026 (20:39 UTC, ronda 1e P4)**
> Estado: **DECIDIDA · PENDIENTE DE MARCAR EN EL LIBRO**
> Va al ADR: ______________
> Toca: `ADR-027` §1-§5 · `ADR-024` §1 (`corrige_a` en la tupla) · `ESC-34`

## 1 · Lo decidido
Juan: «Deprecia ADR-027 bajo el motivo de mal conceptualizado».

## 2 · Los motivos
El modelo de ADR-027 se apoyaba en correcciones que viajan desde el celular (`corrige_a`). En el
detallamiento quedó que **el operario no corrige**: edita lo no enviado o recaptura, y corrige solo
el gerente de producción (ver `IA_2026-10-16-corrige-solo-el-gerente-de-produccion.md`).

## 3 · Lo que queda en el aire
- Falta marcarlo en `ADR.xlsx` y en el documento de alternativa de solución, y decidir qué reemplaza sus §1-§5.
- Mientras tanto la ESPEC del contrato sostiene como decisiones propias lo que venía de ADR-027: identidad UUID v7 (A-1, A-38), los tres casos de idempotencia (A-2), la clave del hecho (A-3), la huella (A-19) y el consecutivo reiniciado (A-39).
- `ADR-024` §1 sigue poniendo `corrige_a` en la tupla del evento (K-47).
