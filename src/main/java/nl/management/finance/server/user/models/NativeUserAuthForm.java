package nl.management.finance.server.user.models;

import nl.management.finance.server.user.exceptions.FormInvalidException;

public class NativeUserAuthForm {
    private String username;
    private String password;

    public NativeUserAuthForm() {

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
