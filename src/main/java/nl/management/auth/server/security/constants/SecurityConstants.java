package nl.management.auth.server.security.constants;

public class SecurityConstants {
    public static final String AUTH_LOGIN_URL = "/authenticate/native";
    public static final String GOOGLE_AUTH_LOGIN_URL = "/authenticate/google";

    // Signing key for HS512 algorithm
    public static final String JWT_SECRET = "n2r5u8x/A%D*G-KaPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq4t7w!z%C*F-J@NcRf";

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "management-finance-api";
    public static final String TOKEN_AUDIENCE = "management-finance-app";

    private SecurityConstants() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }
}
