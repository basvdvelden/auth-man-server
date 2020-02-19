package nl.management.auth.server.user;

import nl.management.auth.server.user.jwt.TokenConstants;
import nl.management.auth.server.user.models.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public void register(NativeUserRegistrationReqDto dto) {
        service.register(dto);
    }

    @PostMapping("/{uuid}/logout")
    public void logout(@PathVariable("uuid") UUID uuid, HttpServletRequest request) {
        String accessToken = request.getHeader(TokenConstants.TOKEN_HEADER).replace(TokenConstants.TOKEN_PREFIX, "");
        service.logout(uuid, accessToken);
    }

    @PostMapping("/authenticate/native")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthDto authenticateNative(NativeUserAuthReqDto dto) {
        return service.authenticate(dto);
    }

    @PostMapping("/authenticate/google")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthDto authenticateGoogle(GoogleUserAuthReqDto dto) throws Exception {
        return service.authenticate(dto);
    }

    @PostMapping(value = "/{uuid}/token/refresh", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TokenRefreshResDto refreshToken(@PathVariable("uuid") UUID uuid, @RequestBody String refreshToken, HttpServletRequest request) {
        String accessToken = request.getHeader(TokenConstants.TOKEN_HEADER).replace(TokenConstants.TOKEN_PREFIX, "");
        return service.refreshToken(uuid, refreshToken, accessToken);
    }
}
