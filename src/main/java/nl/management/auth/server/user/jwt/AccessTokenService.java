package nl.management.auth.server.user.jwt;

import io.jsonwebtoken.*;
import nl.management.auth.server.exceptions.AccessTokenCreationFailedException;
import nl.management.auth.server.exceptions.JWTParsingFailedException;
import nl.management.auth.server.user.models.entities.ERole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class AccessTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenService.class);

    // 10 sec
    private final static Integer EXPIRATION = 1000000;

    private final JedisService jedisService;

    @Autowired
    public AccessTokenService(JedisService jedisService) {
        this.jedisService = jedisService;
    }

    String createAccessToken(UUID userId) {
        try (InputStream privateKeyStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("jwt/auth-private.der"))) {

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyStream.readAllBytes());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(keySpec);
            String jwt = Jwts.builder()
                    .signWith(privateKey, SignatureAlgorithm.RS512)
                    .setHeaderParam("typ", TokenConstants.TOKEN_TYPE)
                    .setIssuer(TokenConstants.TOKEN_ISSUER)
                    .setAudience(TokenConstants.TOKEN_AUDIENCE)
                    .setSubject(userId.toString())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                    .claim("rol", Arrays.asList(ERole.TRIAL.name()))
                    .compact();
            return jwt;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOG.error("Exception thrown while creating access token. ERROR:", e);
            throw new AccessTokenCreationFailedException("Could not create access token!");
        }
    }

    void invalidate(String accessToken) {
        double exp = extractExp(accessToken).doubleValue();
        if (exp > System.currentTimeMillis()) {
            jedisService.addToBlacklist(exp, accessToken);
        }
    }

    Long extractExp(String accessToken) {
        Jws<Claims> parsedToken;
        try {
            parsedToken = getClaims(accessToken);
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration().getTime();
        }
        return parsedToken.getBody().getExpiration().getTime();
    }

    private Jws<Claims> getClaims(String accessToken) {
        try (InputStream publicKeyStream = getClass().getClassLoader().getResourceAsStream("jwt/auth-public.der")) {
            assert publicKeyStream != null;

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyStream.readAllBytes());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(keySpec);
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(accessToken);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOG.error("Exception was thrown while parsing jwt to get claims. ERROR:", e);
            throw new JWTParsingFailedException("Error parsing jwt", e);
        }
    }
}
