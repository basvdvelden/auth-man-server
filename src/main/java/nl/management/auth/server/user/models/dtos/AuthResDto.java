package nl.management.auth.server.user.models.dtos;

import java.util.UUID;

public class AuthResDto {
    private String accessToken;
    private String refreshToken;
    private String displayName;
    private UUID uuid;
    private Boolean activeUser;

    public AuthResDto(String accessToken, String refreshToken, String displayName, UUID uuid, Boolean activeUser) {
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
        setDisplayName(displayName);
        setUuid(uuid);
        setActiveUser(activeUser);
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Boolean isActiveUser() {
        return activeUser;
    }

    public void setActiveUser(Boolean activeUser) {
        this.activeUser = activeUser;
    }
}
