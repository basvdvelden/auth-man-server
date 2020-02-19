package nl.management.auth.server.exceptions;

public class JWTParsingFailedException extends RuntimeException {
    public JWTParsingFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
