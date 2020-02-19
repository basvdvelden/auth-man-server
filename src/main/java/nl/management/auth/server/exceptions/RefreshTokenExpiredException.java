package nl.management.auth.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Refresh token expired!")
public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String msg) {
        super(msg);
    }
}
