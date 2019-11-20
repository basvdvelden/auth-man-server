package nl.management.auth.server.user.models.dtos;

public class RefreshTokenReqDto {
    private String refreshToken;

    public RefreshTokenReqDto() {

    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
