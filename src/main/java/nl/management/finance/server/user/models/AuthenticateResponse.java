package nl.management.finance.server.user.models;

import nl.management.finance.server.common.jwt.models.AccessTokenValue;
import nl.management.finance.server.common.jwt.models.RefreshTokenValue;

public class AuthenticateResponse {
    private RefreshTokenValue refreshTokenValue;
    private AccessTokenValue accessTokenValue;

    public AuthenticateResponse(RefreshTokenValue refreshTokenValue, AccessTokenValue accessTokenValue) {
        setRefreshTokenValue(refreshTokenValue);
        setAccessTokenValue(accessTokenValue);
    }

    public RefreshTokenValue getRefreshTokenValue() {
        return refreshTokenValue;
    }

    public void setRefreshTokenValue(RefreshTokenValue refreshTokenValue) {
        this.refreshTokenValue = refreshTokenValue;
    }

    public AccessTokenValue getAccessTokenValue() {
        return accessTokenValue;
    }

    public void setAccessTokenValue(AccessTokenValue accessTokenValue) {
        this.accessTokenValue = accessTokenValue;
    }
}
