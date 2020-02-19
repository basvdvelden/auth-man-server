package nl.management.auth.server.user.models.dtos;

public class TokenRefreshResDto {
    private String accessToken;
    private Long expiresAt;
    private String refreshToken;

    public TokenRefreshResDto(String accessToken, Long expiresAt, String refreshToken) {
        setAccessToken(accessToken);
        setExpiresAt(expiresAt);
        setRefreshToken(refreshToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
