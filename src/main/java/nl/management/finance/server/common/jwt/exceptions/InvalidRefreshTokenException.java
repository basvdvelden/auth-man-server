package nl.management.finance.server.common.jwt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
        reason = "Invalid refresh token!")
public class InvalidRefreshTokenException extends Exception {
    public InvalidRefreshTokenException(String msg) {
        super(msg);
    }
}
