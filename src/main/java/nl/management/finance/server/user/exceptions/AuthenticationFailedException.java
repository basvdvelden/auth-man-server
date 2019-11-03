package nl.management.finance.server.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Authentication failed!")
public class AuthenticationFailedException extends Exception {
    public AuthenticationFailedException(String msg) {
        super(msg);
    }
}
