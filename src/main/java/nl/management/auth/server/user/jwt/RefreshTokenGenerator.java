package nl.management.auth.server.user.jwt;

import java.security.SecureRandom;
import java.util.Locale;

public class RefreshTokenGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] SYMBOLS = ALPHANUM.toCharArray();
    private static final char[] BUFFER = new char[60];

    public static String generate() {
        return getRandomString();
    }

    private static String getRandomString() {
        for (int idx = 0; idx < BUFFER.length; ++idx)
            BUFFER[idx] = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
        return new String(BUFFER);
    }
}
