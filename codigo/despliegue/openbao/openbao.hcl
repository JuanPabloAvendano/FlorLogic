# IA_ · Configuración de OpenBao, el vault del nodo de la finca (CMP-20, TEC-33)
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md (los A-n citados abajo)
# La imagen la copia a /openbao/config/; docker-entrypoint.sh arranca «bao server -config=/openbao/config».

# Almacenamiento integrado de un solo nodo, en el volumen bao-datos: OpenBao 2.7 ya no acepta storage "file" (A-12).
storage "raft" {
  path    = "/openbao/data"
  node_id = "finca-1"
}

# Sin cluster_addr raft no arranca (COH-33).
api_addr     = "http://openbao:8200"
cluster_addr = "http://openbao:8201"

# Sin TLS: solo lo alcanzan backend y openbao-init por la red interna de Docker, sin ports: (A-27, C-2). El TLS llega
# con la CA de la finca (TEC-16).
listener "tcp" {
  address     = "0.0.0.0:8200"
  tls_disable = true
}

ui = false

# El sello pkcs11 es un complemento externo (TEC-35): se declara aquí porque un kms tiene que existir con el vault
# sellado (OpenBao 2 · Docs · secc. 367). OpenBao comprueba el sha256 del binario antes de ejecutarlo; la firma .sig
# se comprueba en el Segmento 7 (COH-41). Sin env: el complemento hereda BAO_HSM_LIB y BAO_HSM_PIN del servidor
# (secc. 128).
plugin_directory = "/openbao/plugins"

plugin "kms" "pkcs11" {
  command   = "openbao-plugin-kms-pkcs11"
  version   = "v0.2.1"
  sha256sum = "e88632ee52600513bd0e77242823ef57641f1eaac019d8c66515c8bca81e007d"
}

# La llave del vault vive en el token PKCS#11: SoftHSM fuera de la finca, el token HSM USB en la finca; cambia solo
# BAO_HSM_LIB (A-22, A-27). El PIN no va aquí: arranque.sh lo exporta como BAO_HSM_PIN desde el Compose secret (A-38).
seal "pkcs11" {
  token_label = "florlogic"
  key_label   = "bao-sello-1"
  mechanism   = "AES_GCM"

  # Con el token USB guardado, el registro dice «la llave no está» una vez por hora, no cada 10 min (A-39).
  health_check_enabled            = true
  health_check_interval           = "1h"
  health_check_interval_unhealthy = "1h"
}
