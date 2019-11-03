package nl.management.finance.server.common.jwt;

import nl.management.finance.server.common.exceptions.EntityNullException;
import nl.management.finance.server.common.exceptions.InternalServerErrorException;
import nl.management.finance.server.common.jwt.exceptions.RefreshTokenDoesNotExistForGivenUUIDException;
import nl.management.finance.server.common.jwt.exceptions.RefreshTokenExpiredException;
import nl.management.finance.server.common.jwt.exceptions.InvalidRefreshTokenException;
import nl.management.finance.server.common.jwt.models.RefreshToken;
import nl.management.finance.server.common.jwt.models.RefreshTokenValue;
import nl.management.finance.server.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void update(RefreshToken refreshToken) {
        refreshTokenRepository.flush();
    }

    public RefreshToken save(RefreshToken refreshToken) throws EntityNullException, InternalServerErrorException {
        if (refreshToken == null) {
            throw new EntityNullException("Refresh token was null!");
        }
        try {
            refreshToken = this.refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            throw new InternalServerErrorException(String.format(
                    "Exception while saving refresh token: %s",
                    Arrays.toString(e.getStackTrace())));
        }
        return refreshToken;
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
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

    public RefreshToken toEntity(User user, RefreshTokenValue refreshTokenValue) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue.getValue());
        refreshToken.setUserUuid(user.getUuid());
        refreshToken.setExpires(LocalDateTime.now().plusYears(1L));

        return refreshToken;
    }

    public void verify(UUID uuid, RefreshTokenValue refreshTokenValue) throws Exception {
        RefreshToken refreshToken = getRefreshToken(uuid);
        if (isExpired(refreshToken)) {
            throw new RefreshTokenExpiredException(refreshToken.getToken() + " expired!");
        }
        if (!refreshTokenValue.getValue().equals(refreshToken.getToken())) {
            throw new InvalidRefreshTokenException(refreshTokenValue.getValue() + " does not belong to given uuid: " + uuid);
        }
    }

    public RefreshTokenValue createRefreshTokenValue() {
        return RefreshTokenValueGenerator.getRefreshTokenValue();
    }

    private boolean isExpired(RefreshToken refreshToken) {
        return LocalDateTime.now().isAfter(refreshToken.getExpires());
    }
}
