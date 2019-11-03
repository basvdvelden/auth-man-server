package nl.management.finance.server.user.models;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "google_user")
public class GoogleUser extends User {
    @Column(name = "code")
    private Integer code;

    public GoogleUser() { }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
