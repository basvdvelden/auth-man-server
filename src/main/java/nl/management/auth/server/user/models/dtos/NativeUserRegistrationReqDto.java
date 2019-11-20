package nl.management.auth.server.user.models.dtos;

import nl.management.auth.server.exceptions.FormInvalidException;

public class NativeUserRegistrationReqDto {
    private String username;
    private String password;

    private final static String PASSWORD_VALIDATION_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*_?&]{8,}$";

    public NativeUserRegistrationReqDto() {
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
        boolean valid = passwordValid() && usernameValid();
        if (!valid) {
            throw new FormInvalidException("Form contains invalid data!");
        }
    }

    private boolean passwordValid() {
        return password.matches(PASSWORD_VALIDATION_PATTERN);
    }

    private boolean usernameValid() {
        return username.length() < 60 && username.length() > 5;
    }
}
