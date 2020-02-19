package nl.management.auth.server.user.models.dtos;

import org.springframework.lang.NonNull;

import java.util.UUID;

public class AuthDto {
    private String accessToken;
    private Long expiresAt;
    private String refreshToken;
    private String displayName;
    private UUID userId;
    private Boolean active;
    private String username;

    public AuthDto(String displayName, UUID userId, Boolean active, String username) {
        setDisplayName(displayName);
        setUserId(userId);
        setActive(active);
        setUsername(username);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("active=%s, displayName=%s, username=%s, userId=%s, refreshToken=%s, accessToken=%s",
                active, displayName, username, userId, refreshToken, accessToken);
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
