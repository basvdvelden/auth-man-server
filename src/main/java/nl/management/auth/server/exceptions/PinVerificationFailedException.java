package nl.management.auth.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Pin code was incorrect!")
public class PinVerificationFailedException extends Exception {
    public PinVerificationFailedException(String msg) {
        super(msg);
    }
}
