package nl.management.auth.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "User not logged in!")
public class BlacklistedException extends Exception {
    public BlacklistedException(String msg) {
        super(msg);
    }
}
