package nl.management.finance.server.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nl.management.finance.server.common.jwt.models.AccessTokenValue;
import nl.management.finance.server.security.ERole;
import nl.management.finance.server.security.constants.SecurityConstants;
import nl.management.finance.server.user.models.User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Service
public class AccessTokenService {
    private final static Integer EXPIRATION = 864000000;

    public AccessTokenService() {}

    public AccessTokenValue createAccessToken(User user) {
        return createAccessToken(user.getUuid());
    }

    public AccessTokenValue refresh(AccessTokenValue accessTokenValue) {
        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
        Jws<Claims> parsedToken = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(accessTokenValue.getValue());
        String uuid = parsedToken.getBody().getSubject();
        return createAccessToken(UUID.fromString(uuid));
    }

    public String extractSubject(AccessTokenValue accessTokenValue) {
        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
        Jws<Claims> parsedToken = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(accessTokenValue.getValue());
        return parsedToken.getBody().getSubject();
    }

    private AccessTokenValue createAccessToken(UUID uuid) {
        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
        String jwt = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                .setIssuer(SecurityConstants.TOKEN_ISSUER)
                .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                .setSubject(uuid.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .claim("rol", Arrays.asList(ERole.TRIAL.name()))
                .compact();
        return new AccessTokenValue(jwt);
    }
}
