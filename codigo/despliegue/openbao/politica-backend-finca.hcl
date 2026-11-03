# IA_ · Política de OpenBao del backend de la finca: solo leer su clave (A-12)
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-12, A-35
# openbao-init la escribe como «backend-finca» y la ata al AppRole del mismo nombre. KV v2 guarda los datos bajo
# data/ y la descripción de las versiones bajo metadata/: Spring Cloud Vault solo lee data/.

path "kv/data/florlogic/backend-finca" {
  capabilities = ["read"]
}

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: permiso mínimo del backend en el vault: leer solo su propio secreto.
# Uso: init.sh la carga con «bao policy write backend-finca» y la ata al AppRole backend-finca; el backend entra con
# ese AppRole (Spring Cloud Vault) y solo puede hacer lo que dice aquí.
# Origen: la cabecera de arriba (ESPEC-esqueleto-tx1).
# Línea por línea (lo no obvio):
# - path "kv/data/florlogic/backend-finca": la ruta exacta, sin comodines; «kv» es el motor de secretos KV versión 2
#   montado por init.sh, y «data/» es el prefijo que KV v2 pone a los valores.
# - capabilities = ["read"]: solo lectura; ni listar, ni escribir, ni borrar. Lo no nombrado queda negado.
