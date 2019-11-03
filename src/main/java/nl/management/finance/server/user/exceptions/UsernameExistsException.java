package nl.management.finance.server.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Username is already taken!")
public class UsernameExistsException extends Exception {
    public UsernameExistsException(String msg) {
        super(msg);
    }
}
