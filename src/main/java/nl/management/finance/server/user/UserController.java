package nl.management.finance.server.user;

import nl.management.finance.server.common.jwt.RefreshTokenService;
import nl.management.finance.server.common.jwt.models.AccessTokenValue;
import nl.management.finance.server.common.jwt.models.RefreshToken;
import nl.management.finance.server.common.jwt.models.RefreshTokenValue;
import nl.management.finance.server.security.constants.SecurityConstants;
import nl.management.finance.server.user.models.AuthenticateResponse;
import nl.management.finance.server.user.models.NativeUser;
import nl.management.finance.server.user.models.NativeUserAuthForm;
import nl.management.finance.server.user.models.NativeUserRegistrationForm;
import nl.management.finance.server.common.jwt.AccessTokenService;
import nl.management.finance.server.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(NativeUserRegistrationForm form) throws Exception {
        service.register(form);
    }

    @PostMapping("/authenticate/native")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String authenticateNative(HttpServletResponse response, NativeUserAuthForm form) throws Exception {
        AuthenticateResponse auth = service.authenticate(form);

        response.setHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + auth.getAccessTokenValue().getValue());

        return auth.getRefreshTokenValue().getValue();
    }

    @PostMapping("/authenticate/google")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String authenticateGoogle(HttpServletResponse response, NativeUserAuthForm form) throws Exception {
        AuthenticateResponse auth = service.authenticate(form);

        response.setHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + auth.getAccessTokenValue().getValue());

        return auth.getRefreshTokenValue().getValue();
    }

    @GetMapping("/username")
    public String getUsername() {
        return "basvdvelden13@gmail.com";
    }
}
