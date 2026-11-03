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
api_addr     = "https://openbao:8200"
cluster_addr = "https://openbao:8201"

# HTTPS (A-44): el certificado lo firma la raíz de la finca en el paso 1 de la ceremonia (A-46) y entra como Compose
# secret; arranque.sh lo copia aquí con dueño openbao. Solo lo alcanzan backend y openbao-init, sin ports: (C-2).
listener "tcp" {
  address         = "0.0.0.0:8200"
  tls_cert_file   = "/openbao/tls/servidor.crt"
  tls_key_file    = "/openbao/tls/servidor.key"
  tls_min_version = "tls12"
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

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: configuración del servidor OpenBao: dónde guarda datos, por dónde escucha y con qué llave se sella.
# Uso: la imagen la copia a /openbao/config/ y el servidor la lee al arrancar (bao server -config=/openbao/config).
# No se edita en caliente: un cambio exige reconstruir la imagen o recrear el contenedor.
# Origen: la cabecera de arriba (ESPEC-esqueleto-tx1).
# Línea por línea (lo no obvio):
# - HCL: el lenguaje de configuración de HashiCorp; «bloque "tipo" { clave = valor }».
# - storage "raft": almacenamiento integrado; path es la carpeta de datos (el volumen bao-datos), node_id el nombre
#   de este nodo dentro del clúster de uno.
# - api_addr: la dirección que el vault anuncia a sus clientes. cluster_addr: la que usa entre nodos (puerto 8201);
#   con un solo nodo no se usa, pero raft no arranca sin ella.
# - listener "tcp": address 0.0.0.0:8200 escucha en todas las interfaces del contenedor (solo la red interna de
#   Compose lo alcanza). tls_cert_file/tls_key_file: el certificado que copia arranque.sh. tls_min_version = "tls12":
#   rechaza TLS 1.0 y 1.1.
# - ui = false: apaga la interfaz web del vault; todo se hace por CLI desde init.sh.
# - plugin_directory: la carpeta donde el Dockerfile dejó el binario del complemento.
# - plugin "kms" "pkcs11": registra el complemento de sello; command es el nombre del binario, version la que se
#   espera y sha256sum la huella que OpenBao comprueba antes de ejecutarlo.
# - seal "pkcs11": el vault no guarda su llave maestra en disco; la cifra con una llave que vive en el token PKCS#11.
#   token_label y key_label deben coincidir con TOKEN y LLAVE de arranque.sh. mechanism = "AES_GCM": cifrado AES con
#   autenticación.
# - health_check_*: cada cuánto el sello comprueba que el token sigue presente (sano y no sano: una vez por hora).
