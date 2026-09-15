# `SPK-13` · Cifrado y respaldo, como conjunto

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026
> Estado: **SIN REVISAR** · Chat 3 de 3
> Manda sobre esto: `ADR-012`, `ADR-003`, `ADR-022` · `CN-15`, `CN-28`, `CN-03` · `ESC-19`, `ESC-50`

## Qué se explora

Cómo se cifra —en tránsito y en reposo—, **quién custodia la llave**, y cómo se respalda y se
restaura. **Van juntos a propósito:** la llave del cliente es justamente lo que hace posible que un
respaldo salga de la finca sin que quien lo guarda pueda leerlo. Separarlos deja las tres decisiones
a medias.

## Lo que manda

- **`ADR-012`** (Aceptada) — la llave es **de cada empresa**. Además el equipo conserva una **copia de
  custodia fuera de línea, en soporte físico, con dos ejemplares en sitios separados**. El cliente
  firma sabiendo que esa copia existe y qué la dispara.
- **`CN-28`** — la nube guarda respaldos **sin poder leerlos**. **Descifra la finca, no la nube.**
- **`ADR-003`** — aislamiento por empresa en varias capas; la gobernanza del dato es de la empresa.
- **`CN-15`** — pérdida **cero** y **restauración en 1 día o menos**.
- **`ESC-19`** — el respaldo lleva **prueba de restauración**, no solo respaldo.
- **`ESC-50`** — 0 accesos a datos de negocio en operación normal · 100% de los accesos excepcionales
  registrados y autorizados · respaldos cifrados en el **100%** de los casos.
- **`ADR-022`** — se conserva **todo** durante 5 años, así que el respaldo crece; y el dimensionamiento
  se quedó sin suelo cuando se rechazó `ADR-033`.

## Qué se mide

1. **Cifrado en reposo de la base:** a nivel de volumen frente a a nivel de base. **Costo medido en
   rendimiento**, no estimado — y contrastado contra el pico de ingesta que mide `SPK-12`.
2. **Respaldo cifrado con la llave del cliente y restauración completa**, cronometrada contra el
   «1 día o menos» de `CN-15`.
3. **Prueba de restauración automática** (`ESC-19`): que el propio respaldo se verifique solo.
4. **Que quien guarda no pueda leer: demuéstralo, no lo afirmes.** Intenta abrir el respaldo sin la
   llave y deja la evidencia. Es la única forma de sostener `CN-28`.
5. **Crecimiento del respaldo por ciclo de producción**, con retención de 5 años.

## Candidatos a mirar

Cifrado de volumen (LUKS/dm-crypt) frente a cifrado dentro de la base · pgBackRest o Barman frente a
Restic o Borg · custodia de la llave: HashiCorp Vault u OpenBao autoalojado frente a una custodia más
simple. **`[!]` El KMS del proveedor de nube está descartado por escrito:** si la llave la administra
quien custodia la copia, `CN-28` queda vacía.

## `[!]` Aviso de alcance — mide dos hipótesis

Buena parte de esta pieza existe **porque hay una nube que guarda respaldos**. Si `N4` se difiere
—propuesta abierta desde el 10-sep—, el respaldo es a disco local o a la nube del propio cliente, y
el Key Vault autoalojado pierde su razón de ser.

**Mide las dos hipótesis, o como mínimo di explícitamente cuál de tus conclusiones se cae si `N4` se
difiere.** No decidas el alcance: eso es de Juan.

## `[!]` Dos cosas que hay que anotar y no inventar

1. **El aviso de `ADR-012` no está definido.** Cada uso de la copia de custodia debe avisarse al
   administrador de la empresa, y **ninguna decisión dice por qué canal ni con qué garantía**. Sin
   ese aviso, la promesa de `ESC-50` («100% notificados») **no se puede demostrar**. Anótalo como
   hueco; no inventes el mecanismo.
2. **El cliente dijo que no.** En `B4` respondió **NO** a respaldos cifrados, **NO** a llave por
   empresa y **NO** a registrar todo acceso técnico. **El equipo decidió que sí**, y es una de las
   siete decisiones tomadas en su contra. Tu resultado debe decirlo, porque cambia la conversación:
   esto no es solo una elección técnica, es una cláusula que alguien tiene que firmar.

## Qué NO se decide aquí

El alcance de `N4` · el canal del aviso de `ADR-012` · la retención (`ADR-022`) · el motor de base de
datos (aplazado esta ronda) · el lenguaje del backend (`SPK-09` lo está explorando sin elegir).
