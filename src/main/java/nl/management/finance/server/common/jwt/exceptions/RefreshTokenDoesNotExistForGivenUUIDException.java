package nl.management.finance.server.common.jwt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
        reason = "Access token invalid!")
public class RefreshTokenDoesNotExistForGivenUUIDException extends Exception {
    public RefreshTokenDoesNotExistForGivenUUIDException(String msg) {
        super(msg);
    }
}
