package nl.management.auth.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Pin code invalid format")
public class InvalidPinCodeException extends Exception {
    public InvalidPinCodeException(String msg) {
        super(msg);
    }
}
