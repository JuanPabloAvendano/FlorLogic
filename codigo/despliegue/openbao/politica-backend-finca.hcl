# IA_ · Política de OpenBao del backend de la finca: solo leer su clave (A-12)
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-12, A-35
# openbao-init la escribe como «backend-finca» y la ata al AppRole del mismo nombre. KV v2 guarda los datos bajo
# data/ y la descripción de las versiones bajo metadata/: Spring Cloud Vault solo lee data/.

path "kv/data/florlogic/backend-finca" {
  capabilities = ["read"]
}
