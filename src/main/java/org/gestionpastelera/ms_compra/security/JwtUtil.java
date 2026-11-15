package org.gestionpastelera.ms_compra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtUtil {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${aws.cognito.region:us-east-1}")
    private String cognitoRegion;

    @Value("${aws.cognito.user-pool-id:}")
    private String cognitoUserPoolId;

    @Value("${aws.cognito.issuer-uri:}")
    private String cognitoIssuerUri;

    private volatile SecretKey cachedSigningKey;
    private final ConcurrentHashMap<String, PublicKey> jwksCache = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private SecretKey getSigningKey() {
        SecretKey key = this.cachedSigningKey;
        if (key == null) {
            synchronized (this) {
                key = this.cachedSigningKey;
                if (key == null) {
                    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
                    key = Keys.hmacShaKeyFor(keyBytes);
                    this.cachedSigningKey = key;
                }
            }
        }
        return key;
    }

    public Claims extractAllClaims(String token) {
        String[] parts = token.split("\\.");
        if (parts.length == 3) {
            try {
                String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
                String alg = extractJsonValue(headerJson, "alg");
                String kid = extractJsonValue(headerJson, "kid");
                // Valida token RS256 Cognito
                if ("RS256".equalsIgnoreCase(alg) && kid != null && !kid.isBlank()) {
                    String issuer = resolveCognitoIssuer();
                    PublicKey publicKey = getCognitoPublicKey(kid, issuer);
                    return Jwts.parser()
                            .verifyWith(publicKey)
                            .requireIssuer(issuer)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
                }
            } catch (JwtException e) {
                throw e;
            } catch (Exception e) {
                throw new JwtException("Error validando token RS256 de AWS Cognito: " + e.getMessage(), e);
            }
        }

        // Valida token HMAC local
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String resolveCognitoIssuer() {
        if (cognitoIssuerUri != null && !cognitoIssuerUri.isBlank()) {
            return cognitoIssuerUri.trim().replaceAll("/+$", "");
        }
        if (cognitoUserPoolId != null && !cognitoUserPoolId.isBlank()) {
            String region = (cognitoRegion != null && !cognitoRegion.isBlank()) ? cognitoRegion.trim() : "us-east-1";
            return "https://cognito-idp." + region + ".amazonaws.com/" + cognitoUserPoolId.trim();
        }
        throw new JwtException("AWS Cognito no está configurado (COGNITO_USER_POOL_ID o COGNITO_ISSUER_URI requeridos para RS256)");
    }

    private PublicKey getCognitoPublicKey(String kid, String issuer) {
        PublicKey cached = jwksCache.get(kid);
        if (cached != null) {
            return cached;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(issuer + "/.well-known/jwks.json"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new JwtException("No se pudo descargar JWKS de Cognito (HTTP " + response.statusCode() + ")");
            }
            String body = response.body();
            Matcher keyObjectMatcher = Pattern.compile("\\{[^{}]*\\}").matcher(body);
            while (keyObjectMatcher.find()) {
                String keyBlock = keyObjectMatcher.group();
                String blockKid = extractJsonValue(keyBlock, "kid");
                String nStr = extractJsonValue(keyBlock, "n");
                String eStr = extractJsonValue(keyBlock, "e");
                if (blockKid != null && nStr != null && eStr != null) {
                    byte[] nBytes = Base64.getUrlDecoder().decode(nStr);
                    byte[] eBytes = Base64.getUrlDecoder().decode(eStr);
                    RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(1, nBytes), new BigInteger(1, eBytes));
                    PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(spec);
                    jwksCache.put(blockKid, pubKey);
                }
            }
            PublicKey resolved = jwksCache.get(kid);
            if (resolved == null) {
                throw new JwtException("No se encontró clave pública para kid: " + kid);
            }
            return resolved;
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtException("Fallo al obtener clave pública JWKS de AWS Cognito", e);
        }
    }

    private String extractJsonValue(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    public String extractRutFromClaims(Claims claims) {
        if (claims == null) {
            return null;
        }
        String[] candidateKeys = {"custom:rut", "rut", "preferred_username", "cognito:username", "username"};
        for (String key : candidateKeys) {
            String value = claims.get(key, String.class);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return claims.getSubject();
    }

    public String extractRolFromClaims(Claims claims) {
        if (claims == null) {
            return null;
        }
        String customRol = claims.get("custom:rol", String.class);
        if (customRol != null && !customRol.isBlank()) {
            return stripRolePrefix(customRol);
        }
        String rol = claims.get("rol", String.class);
        if (rol != null && !rol.isBlank()) {
            return stripRolePrefix(rol);
        }
        Object groups = claims.get("cognito:groups");
        if (groups instanceof Collection<?> groupCollection && !groupCollection.isEmpty()) {
            Object first = groupCollection.iterator().next();
            if (first != null && !first.toString().isBlank()) {
                return stripRolePrefix(first.toString());
            }
        }
        return null;
    }

    private String stripRolePrefix(String role) {
        String trimmed = role.trim();
        return trimmed.startsWith("ROLE_") ? trimmed.substring(5) : trimmed;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractRut(String token) {
        return extractClaim(token, this::extractRutFromClaims);
    }

    public String extractRol(String token) {
        return extractClaim(token, this::extractRolFromClaims);
    }

    public boolean isTokenValid(Claims claims) {
        return claims != null && claims.getExpiration() != null && claims.getExpiration().after(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return isTokenValid(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
