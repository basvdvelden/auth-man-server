package nl.management.finance.server.user.models;

import javax.persistence.*;

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
}
