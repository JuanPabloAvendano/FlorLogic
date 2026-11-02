# IA_ · Política de OpenBao de openbao-init en las corridas siguientes: solo emitir certificados de servidor (K-47)
# Quién: IA (Claude), 1-nov-2026, a pedido de Juan (HTTPS, A-44) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md K-47, A-53
# openbao-init la escribe como «emisor-certificados» y la ata al AppRole del mismo nombre. Nada de KV ni de sys/: con
# el raíz revocado (A-24), esto es lo único que puede hacer una corrida siguiente.

path "pki_int/issue/servidor-finca" {
  capabilities = ["update"]
}
