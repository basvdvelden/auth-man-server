package nl.management.auth.server.user.jwt;

import nl.management.auth.server.user.models.entities.User;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accessToken;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expires;

    @Column(unique = true)
    private String oldRefreshToken;

    @Column(unique = true, nullable = false)
    @NaturalId
    @Type(type="uuid-char")
    private UUID userUuid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID accessToken) {
        this.userUuid = accessToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static RefreshToken fromStringAndUser(@NonNull String accessToken, @NonNull String token, @NonNull User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccessToken(accessToken);
        refreshToken.setToken(token);
        refreshToken.setUserUuid(user.getUuid());
        refreshToken.setExpires(LocalDateTime.now().plusYears(1L));

        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOldRefreshToken() {
        return oldRefreshToken;
    }

    public void setOldRefreshToken(String oldRefreshToken) {
        this.oldRefreshToken = oldRefreshToken;
    }
}
