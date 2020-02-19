package nl.management.auth.server.user.jwt;

import nl.management.auth.server.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    String authenticate(@NonNull UUID userId, String accessToken) {
        String refreshTokenString = generateRefreshToken();
        RefreshToken refreshToken = RefreshToken.fromStringAndUser(accessToken, refreshTokenString, userId);
        refreshTokenRepository.save(refreshToken);

        return refreshTokenString;
    }

    void deleteForUUID(UUID uuid) throws RefreshTokenDoesNotExistForGivenUUIDException {
        RefreshToken refreshToken = getRefreshToken(uuid);
        delete(refreshToken);
    }

    RefreshToken getRefreshToken(UUID uuid) {
        RefreshToken refreshToken;
        refreshToken = refreshTokenRepository.findByUserUuid(uuid);
        if (refreshToken == null) {
            throw new RefreshTokenDoesNotExistForGivenUUIDException(String.format(
                    "refresh token could not be found, uuid: %s", uuid));
        }
        return refreshToken;
    }

    private RefreshToken verify(@NonNull UUID uuid, @NonNull String refreshTokenString, @NonNull String accessToken) {

        RefreshToken refreshToken = getRefreshToken(uuid);
        if (refreshToken == null) {
            throw new InvalidRefreshTokenException(String.format("refresh token is null for uuid: %s", uuid.toString()));
        }
        if (isExpired(refreshToken)) {
            throw new RefreshTokenExpiredException(refreshToken.getToken() + " expired!");
        }
        if (!refreshTokenString.equals(refreshToken.getToken())) {
            throw new InvalidRefreshTokenException(refreshTokenString.concat(" does not belong to given uuid: " + uuid));
        }
        if (refreshTokenString.equals(refreshToken.getOldRefreshToken())) {
            throw new RefreshTokenStolenException(refreshToken.getOldRefreshToken().concat(" was stolen, user must be immediately logged out!"));
        }
        if (!accessToken.equals(refreshToken.getAccessToken())) {
            throw new InvalidAccessTokenException("access token does not match the entity's access token");
        }
        return refreshToken;
    }

    @NonNull
    private String generateRefreshToken() {
        return RefreshTokenGenerator.generate();
    }

    private void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    private boolean isExpired(RefreshToken refreshToken) {
        return LocalDateTime.now().isAfter(refreshToken.getExpires());
    }

    public String refresh(String newAccessToken, UUID uuid, String oldRefreshTokenString, String accessToken) {

        RefreshToken refreshToken = verify(uuid, oldRefreshTokenString, accessToken);

        String refreshTokenString = generateRefreshToken();

        refreshToken.setAccessToken(newAccessToken);
        refreshToken.setToken(refreshTokenString);
        refreshToken.setExpires(LocalDateTime.now().plusYears(1L));
        refreshToken.setOldRefreshToken(oldRefreshTokenString);
        refreshTokenRepository.save(refreshToken);

        return refreshTokenString;
    }
}
