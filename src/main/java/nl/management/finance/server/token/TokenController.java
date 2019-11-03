package nl.management.finance.server.token;

import nl.management.finance.server.common.jwt.RefreshTokenService;
import nl.management.finance.server.common.jwt.models.AccessTokenValue;
import nl.management.finance.server.common.jwt.models.RefreshTokenValue;
import nl.management.finance.server.security.constants.SecurityConstants;
import nl.management.finance.server.common.jwt.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/token")
public class TokenController {
    private RefreshTokenService service;

    @Autowired
    public TokenController(RefreshTokenService service) {
        this.service = service;
    }

    @PostMapping(value = "/refresh", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void refreshToken(@RequestBody String refreshTokenString, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tokenHeader = request.getHeader(SecurityConstants.TOKEN_HEADER);
        String token = tokenHeader.replace(SecurityConstants.TOKEN_PREFIX, "");

        AccessTokenValue accessTokenValue = new AccessTokenValue(token);
        RefreshTokenValue refreshTokenValue = new RefreshTokenValue(refreshTokenString);

        AccessTokenService accessTokenService = new AccessTokenService();
        String uuid = accessTokenService.extractSubject(accessTokenValue);
        service.verify(UUID.fromString(uuid), refreshTokenValue);
        accessTokenValue = accessTokenService.refresh(accessTokenValue);
        response.setHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + accessTokenValue.getValue());
    }
}
