package nl.management.auth.server.user;

import nl.management.auth.server.exceptions.JWTParsingFailedException;
import nl.management.auth.server.user.jwt.TokenConstants;
import nl.management.auth.server.user.models.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(NativeUserRegistrationReqDto dto) throws Exception {
        service.register(dto);
    }

    @PostMapping("/{uuid}/logout")
    public void logout(@PathVariable("uuid") UUID uuid, HttpServletRequest request) throws JWTParsingFailedException {
        String accessToken = request.getHeader(TokenConstants.TOKEN_HEADER).replace(TokenConstants.TOKEN_PREFIX, "");
        service.logout(uuid, accessToken);
    }

    @PostMapping("/authenticate/native")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthResDto authenticateNative(NativeUserAuthReqDto dto) throws Exception {
        return service.authenticate(dto);
    }

    @PostMapping("/authenticate/google")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthResDto authenticateGoogle(GoogleUserAuthReqDto dto) throws Exception {
        return service.authenticate(dto);
    }

    @PostMapping("/{uuid}/token/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenRefreshResDto refreshToken(@PathVariable("uuid") UUID uuid, RefreshTokenReqDto dto, HttpServletRequest request) throws Exception {
        String accessToken = request.getHeader(TokenConstants.TOKEN_HEADER).replace(TokenConstants.TOKEN_PREFIX, "");
        return service.refreshToken(uuid, dto, accessToken);
    }

    @GetMapping("/username")
    public String dummy() {
        return "basvdvelden13@gmail.com";
    }
}
