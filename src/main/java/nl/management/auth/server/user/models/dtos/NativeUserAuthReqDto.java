package nl.management.auth.server.user.models.dtos;

import nl.management.auth.server.exceptions.FormInvalidException;

public class NativeUserAuthReqDto {
    private String username;
    private String password;

    public NativeUserAuthReqDto() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void validate() throws FormInvalidException {
        boolean valid = username != null && password != null;
        if (!valid) {
            throw new FormInvalidException("Missing fields in authentication form!");
        }
    }
}
