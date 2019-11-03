package nl.management.finance.server.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error occured while handling the request.")
public class EntityNullException extends Exception {
    public EntityNullException(String msg) {
        super(msg);
    }
}
