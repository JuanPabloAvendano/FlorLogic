package co.florlogic.finca;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.test.context.DynamicPropertyRegistry;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;

/**
 * Un Keycloak de mentira para las pruebas del backend (CT-2): una llave RSA y su JWKS servido en 127.0.0.1, con el
 * que el backend valida los tokens por la misma cadena que en el nodo (firma, vigencia, emisor y audiencia). Así las
 * pruebas no necesitan Keycloak; el de verdad lo prueban las de extremo a extremo.
 *
 * <p>Una sola instancia por JVM: las pruebas de Spring comparten contexto y, con él, la URL de las llaves.
 */
public final class EmisorDePrueba {

    public static final String EMISOR = "http://localhost/auth/realms/florlogic";
    public static final String AUDIENCIA = "backend-finca";
    /** La persona que entrega en las pruebas: el {@code sub} del token y la que queda en la sesión. */
    public static final String PERSONA = "op-prueba";

    private static final EmisorDePrueba UNICO = new EmisorDePrueba(llave());

    private final RSAKey llave;
    private final String urlLlaves;

    private EmisorDePrueba(RSAKey llave) {
        this.llave = llave;
        try {
            HttpServer servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
            byte[] jwks = new JWKSet(llave.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
            servidor.createContext("/certs", intercambio -> {
                intercambio.getResponseHeaders().add("Content-Type", "application/json");
                intercambio.sendResponseHeaders(200, jwks.length);
                try (OutputStream salida = intercambio.getResponseBody()) {
                    salida.write(jwks);
                }
            });
            servidor.start();   // hilo no demonio de la JVM de pruebas; muere con ella
            this.urlLlaves = "http://127.0.0.1:" + servidor.getAddress().getPort() + "/certs";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Para {@code @DynamicPropertySource}: el backend de la prueba confía en este emisor. */
    public static void registrar(DynamicPropertyRegistry propiedades) {
        propiedades.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> UNICO.urlLlaves);
        propiedades.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> EMISOR);
    }

    /** Un token de operador de {@link #PERSONA}, vigente cinco minutos. */
    public static String operador() {
        return token(PERSONA, List.of("operador"));
    }

    public static String token(String persona, List<String> roles) {
        return UNICO.firmar(persona, roles, EMISOR, AUDIENCIA, Instant.now().plus(Duration.ofMinutes(5)), UNICO.llave);
    }

    /** Lo mismo, con cada campo a elección: para los casos de token vencido, de otro emisor o de otra audiencia. */
    public static String token(String persona, List<String> roles, String emisor, String audiencia, Instant vence) {
        return UNICO.firmar(persona, roles, emisor, audiencia, vence, UNICO.llave);
    }

    /** Bien formado y con todo en regla, pero firmado con otra llave: el backend no la conoce. */
    public static String firmadoConOtraLlave() {
        return UNICO.firmar(PERSONA, List.of("operador"), EMISOR, AUDIENCIA, Instant.now().plus(Duration.ofMinutes(5)),
                llave());
    }

    private String firmar(String persona, List<String> roles, String emisor, String audiencia, Instant vence,
            RSAKey con) {
        Instant ahora = Instant.now();
        JWTClaimsSet reclamos = new JWTClaimsSet.Builder()
                .issuer(emisor)
                .subject(persona)
                .audience(audiencia)
                .issueTime(Date.from(ahora.minusSeconds(1)))
                .notBeforeTime(Date.from(ahora.minusSeconds(1)))
                .expirationTime(Date.from(vence))
                .jwtID(UUID.randomUUID().toString())
                .claim("azp", "celular")
                .claim("realm_access", Map.of("roles", roles))
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(con.getKeyID())
                .type(JOSEObjectType.JWT).build(), reclamos);
        try {
            jwt.sign(new RSASSASigner(con));
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
        return jwt.serialize();
    }

    private static RSAKey llave() {
        try {
            // El mismo kid en las dos llaves: el backend no puede descartar la ajena por el kid, tiene que verificar.
            return new RSAKeyGenerator(2048).keyID("florlogic-prueba").generate();
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
    }
}
