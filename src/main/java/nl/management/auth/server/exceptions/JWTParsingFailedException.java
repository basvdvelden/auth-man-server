package nl.management.auth.server.exceptions;

public class JWTParsingFailedException extends Exception {
    public JWTParsingFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
