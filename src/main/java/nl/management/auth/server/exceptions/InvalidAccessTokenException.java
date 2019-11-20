package nl.management.auth.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid access token!")
public class InvalidAccessTokenException extends Exception {
    public InvalidAccessTokenException(String msg) {
        super(msg);
    }
}
