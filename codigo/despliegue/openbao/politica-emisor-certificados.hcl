# IA_ · Política de OpenBao de openbao-init en las corridas siguientes: solo emitir certificados de servidor (K-47)
# Quién: IA (Claude), 1-nov-2026, a pedido de Juan (HTTPS, A-44) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md K-47, A-53
# openbao-init la escribe como «emisor-certificados» y la ata al AppRole del mismo nombre. Nada de KV ni de sys/: con
# el raíz revocado (A-24), esto es lo único que puede hacer una corrida siguiente.

path "pki_int/issue/servidor-finca" {
  capabilities = ["update"]
}

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: permiso mínimo de openbao-init después de la primera corrida: solo pedir certificados de servidor.
# Uso: init.sh la carga como «emisor-certificados» y la ata al AppRole del mismo nombre; en los arranques siguientes
# init.sh entra con ese AppRole para renovar certificados, porque el token raíz ya fue revocado.
# Origen: la cabecera de arriba (ESPEC-esqueleto-tx1, HTTPS interno).
# Línea por línea (lo no obvio):
# - path "pki_int/issue/servidor-finca": «pki_int» es el motor PKI de la CA intermedia; «issue/servidor-finca» emite
#   un certificado según el rol servidor-finca que define init.sh.
# - capabilities = ["update"]: en OpenBao emitir es un POST, que se autoriza con «update» (no con «create»).
