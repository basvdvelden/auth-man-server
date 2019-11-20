package nl.management.auth.server.exceptions;

public class RefreshTokenStolenException extends Exception {
    public RefreshTokenStolenException(String msg) {
        super(msg);
    }
}
