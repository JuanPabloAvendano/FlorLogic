REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Modelo de amenazas STRIDE de TX-1

**Método.** Se toma el diagrama de contenedores de TX-1 (README §5 nivel 2, más lo que la construcción añade: Keycloak,
OpenBao y ntfy) y se recorre cada elemento con las seis categorías: **S**poofing (suplantar), **T**ampering
(alterar), **R**epudiation (negar haberlo hecho), **I**nformation disclosure (filtrar), **D**enial of service
(tumbar) y **E**levation of privilege (subir de permisos) (OWASP · Threat Modeling Process (STRIDE) · secc. 2,
https://community.owasp.org/Threat_Modeling_Process). Las mitigaciones genéricas por categoría siguen la tabla de esa
misma fuente (secc. 3): autenticación, integridad, auditoría, cifrado, filtrado y limitación de tasa, autorización.

**Columna «Evidencia».** Prueba que ya existe, o «Pendiente (Fx)» con la fase de `codigo/IA_CONSTRUCCION-TX1.md`.

## 1 · Diagrama y fronteras de confianza

```
 [Celular / PWA / simulador] ──(F1) HTTP hoy, HTTPS en F6──► [Gateway Nginx + WAF] ──(F2)──► [backend-finca]
        frontera 1: Wi-Fi de la finca                         única puerta publicada          │   │   │
                                                                                               │   │   └─(F5)─► [ntfy] ─► teléfono admin
                                   [Navegador] ──(F3)──► [Gateway] ──► [Keycloak] ◄──(F4) JWKS─┘   │
                                                                                                   ├─(F6) JDBC──► [PostgreSQL]
                                                                                                   └─(F7)──────► [OpenBao + HSM]
        frontera 2: red de Docker del nodo (172.30.0.0/24)       frontera 3: el anfitrión (volúmenes, secretos)
```

Fronteras: (1) el Wi-Fi de la finca, que no es de confianza; (2) la red interna de Docker; (3) el anfitrión, donde
viven los volúmenes y el secreto cero.

## 2 · Amenazas por elemento

### 2.1 Celular / PWA / simulador

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| S | Un equipo ajeno se hace pasar por un celular de la finca | Token OIDC de Keycloak (cliente público con PKCE, CT-2); celular desconocido → la sesión entra marcada | Pendiente (F3). Hoy: un solo dispositivo configurado (`DispositivoDelNodo`, A-107) |
| T | Alguien modifica la jornada guardada en el celular antes de enviarla | Almacén local cifrado (README N3 de la app); huella por anotación (ADR-027) | Pendiente (F8, F1) |
| R | El operador niega haber capturado algo | Autor por sesión de captura (ADR-035: «en este momento de captura, esta persona fue la responsable») | Pendiente (F1, F3) |
| I | Se roba el celular y se lee la jornada | Cifrado del almacén local; sesión que caduca (ESC-28) | Pendiente (F8) |
| D | El celular llena su almacenamiento | Aviso con margen de una jornada (ESC-35) | Pendiente (F8) |
| E | El operador usa el token para endpoints del gerente | Roles en el JWT, verificados en el backend | Pendiente (F3) |

### 2.2 Flujo F1: celular → gateway (Wi-Fi de la finca)

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| T / I | Hombre en el medio lee o cambia la partición y el token | TLS con la CA de la finca (A-44: «ningún enlace en HTTP en claro») | **Pendiente (F6)**: hoy el gateway escucha HTTP en el 8080 |
| S | Un falso nodo recibe la jornada | El celular confía solo en la CA de la finca (K-46 punto 1, abierto) | Pendiente (F6) |
| D | Inundación de peticiones | `limit_req` en Nginx (BB-06); tope de 1 MiB por cuerpo | Tope: `CanalPorGatewayTest.un_cuerpo_de_mas_de_1_mib_es_413_en_cualquier_ruta`. `limit_req`: pendiente (F6) |

### 2.3 Gateway Nginx + WAF ModSecurity / OWASP CRS

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| T | Inyección (SQL, XSS, comandos) en el cuerpo | CRS 4.30.0 en `On`, cuerpo revisado entero hasta 1 MiB | `CanalPorGatewayTest.un_ataque_es_403_bloqueado_y_no_llega_al_backend`, `…un_ataque_en_la_ultima_anotacion_de_casi_1_mib_es_403` |
| T | Evadir el WAF con UTF-16, BOM o más campos de los que revisa | Reglas 10001–10003 propias | `…un_cuerpo_que_no_es_utf8_simple_es_400_y_no_llega_al_backend`, `…mas_de_10000_campos_es_413_rechazado` |
| T | Recorrido de rutas (`..;`) hacia el backend | Una sola ruta permitida por regex `[^/;]+` | `…una_ruta_con_puntos_es_400_en_problem_json_y_no_una_respuesta_vacia`, `…una_ruta_ajena_es_404_rechazado_y_no_llega_al_backend` |
| I | El registro del WAF guarda el token | `SecRuleUpdateTargetByTag "OWASP_CRS" "!REQUEST_HEADERS:Authorization"`; `log_format` sin `Authorization` ni cuerpo | `…authorization_no_se_revisa_ni_va_al_registro` |
| I | Cabeceras revelan la versión | `server_tokens off`; errores en `problem+json` propios | `…una_ruta_demasiado_larga_es_414_en_problem_json` |
| R | No se puede ligar un bloqueo con la queja del operador | `traceId` del `traceparent` en la respuesta y en el registro | `…el_trace_id_sale_del_traceparent`, `…el_registro_del_waf_lleva_la_traza_y_la_regla_sin_authorization` |
| D | Backend caído tumba al gateway | Gateway arranca solo y responde `502` en `problem+json` | `…con_el_backend_apagado_es_502_no_disponible` |
| E | Acceso a `/actuator` desde la red | Toda ruta ajena es `404` en el gateway | `…una_ruta_ajena_es_404_rechazado_y_no_llega_al_backend` |

### 2.4 Flujo F2 y backend-finca (módulos `ingesta`, `identidad`, `bitacora`…)

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| S | Alguien dentro de la red de Docker llama al backend saltándose el gateway | El backend no publica puerto (C-2) y **verifica el JWT él mismo** (Zero Trust) | Puerto: `NodoCompletoTest.el_gateway_es_la_unica_puerta_del_nodo`. JWT: pendiente (F3) |
| T | Partición con forma inválida | Lector tolerante del contrato: todo fallo de forma es `422` y no guarda nada | `CanalParticionesTest.cualquier_fallo_de_forma_es_422_rechazado_y_no_guarda_nada`, `LectorDtoTest` |
| T | Reenvío con contenido cambiado | Huella y tres casos de ADR-027 | Pendiente (F1) |
| R | Cambios sin autor | Bitácora de solo inserción (ESC-40) | Pendiente (F3) |
| I | Traza de excepción con detalles internos en la respuesta | `ManejadorDeErrores` responde `500` genérico con `Cache-Control: no-store` | `CanalParticionesTest.si_la_base_falla_a_mitad_es_500_generico_y_no_queda_nada_a_medias` |
| D | Partición enorme o JSON muy anidado agota memoria | Tope de 1 MiB en el gateway; profundidad la juzga el backend | `CanalPorGatewayTest.un_json_de_520_niveles_que_el_waf_no_lee_llega_al_backend_y_es_422` |
| E | El controlador accede al dominio o a la base sin pasar por el caso de uso | Reglas ArchUnit | `CapasTest.el_controlador_solo_conoce_el_puerto_de_entrada_y_sus_dto` |
| E | El proceso corre como root | Imagen JRE con usuario 10001 sin shell; JAR de solo lectura (`0444`) | `backend-finca/Dockerfile` (sin prueba automática; Trivy en F6) |

### 2.5 Keycloak (F3)

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| S | Robo de contraseña del gerente o administrador | MFA TOTP obligatorio para esos roles (CT-2) | Pendiente (F3) |
| T | Token alterado | JWT firmado; el backend valida contra el JWKS local | Pendiente (F3) |
| D | Fuerza bruta de contraseñas | Protección de fuerza bruta del realm | Pendiente (F3) |
| E | Consola de administración de Keycloak expuesta | No se publica por el gateway; solo red interna | Pendiente (F3) |

### 2.6 Flujo F6 y PostgreSQL

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| T | Inyección SQL | `JdbcClient` con parámetros con nombre (`:uuid`, `.param(…)`) en `AnotacionRepositorioAdapter` | Indirecta: `AnotacionRepositorioAdapterTest`. Sin prueba de inyección dedicada (ver `IA_OWASP-top10.md` A03) |
| I | Acceso a la base desde la red | Puerto solo en `127.0.0.1` del anfitrión | `compose.yaml` (servicio `base`) |
| I | La clave de la base en el repositorio | Clave en OpenBao; `POSTGRES_PASSWORD_FILE` desde un secreto de Compose | `NodoCompletoTest` (el backend saca su clave de OpenBao) |
| E | El backend usa el superusuario | Rol de la aplicación separado del administrador, con privilegios mínimos | Pendiente (F6). Hoy el backend usa el usuario `florlogic` |
| R / T | Alguien borra filas de la bitácora | `REVOKE UPDATE, DELETE` sobre la tabla de bitácora para el rol de la aplicación | Pendiente (F3) |

### 2.7 Flujo F7 y OpenBao + HSM

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| S | Otro contenedor usa la credencial del backend | AppRole atado a la subred (`secret_id_bound_cidrs`, A-35) | `NodoArranqueSeguroTest.a41_si_la_subred_guardada_no_es_la_del_nodo_para_antes_de_entrar` |
| I | Robo del disco del servidor | Sello `pkcs11`: la llave no sale del token (A-22) | `NodoArranqueSeguroTest.a29_sin_la_bandera_la_imagen_de_openbao_no_crea_llave_en_un_volumen_vacio` |
| E | El backend lee otros secretos | Política de solo `read` sobre `kv/data/florlogic/backend-finca` | `despliegue/openbao/politica-backend-finca.hcl` |
| D | Sin vault el backend queda a medias | `fail-fast`: el backend no arranca y Docker lo reintenta (A-23) | `NodoArranqueSeguroTest.c5_sin_vault_el_backend_reiniciado_no_queda_escuchando` |
| T | Se reescribe la configuración del vault con otra clave | `openbao-init` compara y para | `NodoArranqueSeguroTest.c4_otra_clave_en_el_anfitrion_para_el_arranque_y_no_reescribe_nada` |

### 2.8 Flujo F5 y ntfy (F5)

| STRIDE | Amenaza | Mitigación | Evidencia |
|---|---|---|---|
| I | El aviso push lleva datos de negocio a un servidor ajeno | El aviso dice qué pasó (dispositivo, cuántos pendientes), sin contenido de captura (BB-13) | Pendiente (F5) |
| S | Cualquiera publica en el tema | Tema con token de acceso de ntfy | Pendiente (F5) |
| D | Sin internet el aviso se pierde | Se guarda y `trabajos` (db-scheduler) lo reintenta (CT-3) | Pendiente (F5) |

## 3 · Resumen para la defensa

- **Cubierto con prueba hoy:** gateway y WAF (25 pruebas), arranque seguro del vault (6), idempotencia y
  atomicidad de `ingesta`, capas.
- **Lo más grave abierto:** F1 sin TLS (T/I) y backend sin autenticación (S/E). Los dos tienen fase: F6 y F3.
- **Riesgo aceptado y escrito:** credencial AppRole sin caducidad (A-35).
