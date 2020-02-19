package nl.management.auth.server.exceptions;

public class AccessTokenCreationFailedException extends RuntimeException {
    public AccessTokenCreationFailedException(String msg) {
        super(msg);
    }
}
