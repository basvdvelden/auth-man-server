package nl.management.auth.server.exceptions;

public class AccessTokenCreationFailedException extends Exception {
    public AccessTokenCreationFailedException(String msg) {
        super(msg);
    }
}
