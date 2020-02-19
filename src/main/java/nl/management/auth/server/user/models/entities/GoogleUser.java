package nl.management.auth.server.user.models.entities;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "google_user")
public class GoogleUser extends User {
    @Column
    private String googleUserId;

    public GoogleUser() { }

    public String getGoogleUserId() {
        return googleUserId;
    }

    public void setGoogleUserId(String code) {
        this.googleUserId = code;
    }

    public static GoogleUser fromGooglePayload(GoogleIdToken.Payload payload) {
        GoogleUser result = new GoogleUser();
        UUID uuid = UUID.randomUUID();

        result.setGoogleUserId(payload.getSubject());
        result.setUsername(payload.getEmail());
        result.setUuid(uuid);
        // google users dont need to activate by verifying email
        result.setActive(true);
        result.setName((String)payload.get("name"));
        return result;
    }
}
