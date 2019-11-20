package nl.management.auth.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "match could not be found for this uuid")
public class UUIDInvalidException extends Exception {
    public UUIDInvalidException(String msg) {
        super(msg);
    }
}
