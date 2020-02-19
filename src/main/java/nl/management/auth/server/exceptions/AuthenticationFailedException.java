package nl.management.auth.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Authentication failed!")
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String msg) {
        super(msg);
    }
}
