package nl.management.auth.server.user.jwt;

import nl.management.auth.server.exceptions.*;
import nl.management.auth.server.user.models.dtos.AuthResDto;
import nl.management.auth.server.user.models.dtos.RefreshTokenReqDto;
import nl.management.auth.server.user.models.dtos.TokenRefreshResDto;
import nl.management.auth.server.user.models.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private RefreshTokenRepository refreshTokenRepository;
    private AccessTokenService accessTokenService;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, AccessTokenService accessTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessTokenService = accessTokenService;
    }

    public AuthResDto authenticate(@NonNull User user) throws AccessTokenCreationFailedException {
        String accessToken = accessTokenService.createAccessToken(user);
        String refreshTokenString = generateRefreshToken();
        RefreshToken refreshToken = RefreshToken.fromStringAndUser(accessToken, refreshTokenString, user);
        refreshTokenRepository.save(refreshToken);

        return new AuthResDto(accessToken, refreshTokenString, user.getName(), user.getUuid(), user.isActive());
    }

    public void deleteForUUID(UUID uuid) throws RefreshTokenDoesNotExistForGivenUUIDException {
        RefreshToken refreshToken = getRefreshToken(uuid);
        delete(refreshToken);
    }

    public RefreshToken getRefreshToken(UUID uuid) throws RefreshTokenDoesNotExistForGivenUUIDException {
        RefreshToken refreshToken;
        refreshToken = refreshTokenRepository.findByUserUuid(uuid);
        if (refreshToken == null) {
            throw new RefreshTokenDoesNotExistForGivenUUIDException(String.format(
                    "refresh token could not be found for uuid: %s",
                    uuid));
        }
        return refreshToken;
    }

    public RefreshToken verify(UUID uuid, RefreshTokenReqDto dto, String accessToken) throws
            RefreshTokenDoesNotExistForGivenUUIDException,
            RefreshTokenExpiredException,
            InvalidRefreshTokenException,
            InvalidAccessTokenException,
            RefreshTokenStolenException {

        RefreshToken refreshToken = getRefreshToken(uuid);
        if (isExpired(refreshToken)) {
            throw new RefreshTokenExpiredException(refreshToken.getToken() + " expired!");
        }
        if (!dto.getRefreshToken().equals(refreshToken.getToken())) {
            throw new InvalidRefreshTokenException(dto.getRefreshToken().concat(" does not belong to given uuid: " + uuid));
        }
        if (dto.getRefreshToken().equals(refreshToken.getOldRefreshToken())) {
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

    public TokenRefreshResDto refresh(String newAccessToken, UUID uuid, RefreshTokenReqDto dto, String accessToken) throws
            RefreshTokenExpiredException,
            InvalidRefreshTokenException,
            InvalidAccessTokenException,
            RefreshTokenDoesNotExistForGivenUUIDException,
            RefreshTokenStolenException {

        RefreshToken refreshToken = verify(uuid, dto, accessToken);

        String refreshTokenString = generateRefreshToken();

        refreshToken.setAccessToken(newAccessToken);
        refreshToken.setToken(refreshTokenString);
        refreshToken.setExpires(LocalDateTime.now().plusYears(1L));
        refreshToken.setOldRefreshToken(dto.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return new TokenRefreshResDto(newAccessToken, refreshTokenString);
    }
}
