package nl.management.auth.server.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import nl.management.auth.server.user.jwt.TokenConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;

@Component
public class JWTFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(JWTFilter.class);
    private static final List<String> NO_FILTER = Arrays.asList("/users/register", "/users/authenticate", "/token/refresh", "/logout");

    public JWTFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        if (!shouldFilter(req)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = req.getHeader(TokenConstants.TOKEN_HEADER);

        if (token != null && !token.trim().isEmpty() && token.startsWith(TokenConstants.TOKEN_PREFIX)) {
            try {
                try (InputStream publicKeyStream = getClass().getClassLoader().getResourceAsStream("jwt/auth-public.der")) {
                    assert publicKeyStream != null;

                    // TODO: should use token service
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyStream.readAllBytes());
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = kf.generatePublic(keySpec);
                    LOG.debug("parsing jwt...");
                    Jwts.parser()
                            .setSigningKey(publicKey)
                            .parseClaimsJws(token.replace(TokenConstants.TOKEN_PREFIX, ""));

                    filterChain.doFilter(request, response);
                    return;
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            } catch (ExpiredJwtException exception) {
                LOG.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                LOG.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                LOG.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch (SignatureException exception) {
                LOG.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                LOG.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            }
        }
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login!");
    }

    private boolean shouldFilter(HttpServletRequest request) {
        for (String url: NO_FILTER) {
            if (request.getRequestURI().contains(url)) {
                LOG.info("Not filtering : {}", request.getRequestURI());
                return false;
            }
        }
        return true;
    }
}
