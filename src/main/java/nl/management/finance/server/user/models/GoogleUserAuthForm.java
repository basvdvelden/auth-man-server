package nl.management.finance.server.user.models;

import nl.management.finance.server.user.exceptions.FormInvalidException;

public class GoogleUserAuthForm {
    private String username;
    private String idToken;

    public GoogleUserAuthForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void validate() throws FormInvalidException {
        boolean valid = username != null && idToken != null;
        if (!valid) {
            throw new FormInvalidException("username and code was null!");
        }
    }
}
