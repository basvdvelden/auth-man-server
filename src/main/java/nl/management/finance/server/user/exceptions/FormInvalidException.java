package nl.management.finance.server.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Form data invalid!")
public class FormInvalidException extends Exception {
    public FormInvalidException(String msg) {
        super(msg);
    }

    public FormInvalidException() {
    }
}
