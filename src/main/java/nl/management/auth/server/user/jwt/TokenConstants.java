package nl.management.auth.server.user.jwt;

public class TokenConstants {
    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "auth-man-server";
    public static final String TOKEN_AUDIENCE = "man-app";

    private TokenConstants() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }
}
