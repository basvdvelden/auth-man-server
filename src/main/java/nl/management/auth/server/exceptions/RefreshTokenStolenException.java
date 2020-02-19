package nl.management.auth.server.exceptions;

public class RefreshTokenStolenException extends RuntimeException {
    public RefreshTokenStolenException(String msg) {
        super(msg);
    }
}
