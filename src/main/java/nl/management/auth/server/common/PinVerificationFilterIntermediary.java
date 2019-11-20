package nl.management.auth.server.common;

import nl.management.auth.server.common.jwt.AccessTokenService;
import nl.management.auth.server.exceptions.InvalidPinCodeException;
import nl.management.auth.server.exceptions.JWTParsingFailedException;
import nl.management.auth.server.exceptions.PinVerificationFailedException;
import nl.management.auth.server.exceptions.UUIDInvalidException;
import nl.management.auth.server.security.filters.PinVerificationFilter;
import nl.management.auth.server.user.UserService;
import nl.management.auth.server.user.models.dtos.PinCodeReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Intermediary for the {@link PinVerificationFilter} to be able to verify the pin-code
 */
@Service
public class PinVerificationFilterIntermediary {
    private final UserService userService;
    private final AccessTokenService accessTokenService;

    @Autowired
    public PinVerificationFilterIntermediary(UserService userService, AccessTokenService accessTokenService) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;
    }

    public void verifyPin(String token, String pin) throws UUIDInvalidException, InvalidPinCodeException, PinVerificationFailedException, JWTParsingFailedException {
        UUID uuid = UUID.fromString(accessTokenService.extractSub(token));
        PinCodeReqDto dto = new PinCodeReqDto();
        dto.setPin(pin);
        userService.verifyPin(uuid, dto);
    }
}
