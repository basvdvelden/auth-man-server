package nl.management.auth.server.user.jwt;

import nl.management.auth.server.exceptions.RefreshTokenDoesNotExistForGivenUUIDException;
import nl.management.auth.server.user.UserService;
import nl.management.auth.server.user.models.dtos.AuthDto;
import nl.management.auth.server.user.models.dtos.TokenRefreshResDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    @Autowired
    public TokenService(RefreshTokenService refreshTokenService, AccessTokenService accessTokenService) {
        this.refreshTokenService = refreshTokenService;
        this.accessTokenService = accessTokenService;
    }

    public void logout(UUID userId) {
        String accessToken = refreshTokenService.getRefreshToken(userId).getAccessToken();
        logout(userId, accessToken);
    }

    public void logout(UUID userId, String accessToken) {
        refreshTokenService.deleteForUUID(userId);
        accessTokenService.invalidate(accessToken);
    }

    public AuthDto authenticate(AuthDto dto) {
        String accessToken = accessTokenService.createAccessToken(dto.getUserId());
        Long expiresAt = accessTokenService.extractExp(accessToken);
        String refreshToken = refreshTokenService.authenticate(dto.getUserId(), accessToken);
        dto.setAccessToken(accessToken);
        dto.setExpiresAt(expiresAt);
        dto.setRefreshToken(refreshToken);

        return dto;
    }

    public TokenRefreshResDto refresh(String accessToken, String oldRefreshTokenString, UUID userId) {
        String newAccessToken = accessTokenService.createAccessToken(userId);
        Long expiresAt = accessTokenService.extractExp(newAccessToken);
        accessTokenService.invalidate(accessToken);
        String newRefreshTokenString = refreshTokenService.refresh(
                newAccessToken, userId, oldRefreshTokenString, accessToken);

        return new TokenRefreshResDto(newAccessToken, expiresAt, newRefreshTokenString);
    }

    public boolean isLoggedIn(UUID userId) {
        try {
            refreshTokenService.getRefreshToken(userId);
            return true;
        } catch (RefreshTokenDoesNotExistForGivenUUIDException ignored) {
            return false;
        }
    }
}
