package nl.management.auth.server.user.models.entities;

import nl.management.auth.server.user.models.dtos.NativeUserRegistrationReqDto;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "native_user")
public class NativeUser extends User {

    @Column(nullable = false)
    private String password;

    public NativeUser() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NonNull
    public static NativeUser fromRegistrationDto(NativeUserRegistrationReqDto dto, PasswordEncoder encoder) {
        NativeUser result = new NativeUser();
        UUID uuid = UUID.randomUUID();
        result.setUuid(uuid);
        result.setUsername(dto.getUsername());
        result.setName(dto.getUsername());
        result.setPassword(encoder.encode(dto.getPassword()));
        return result;
    }
}
