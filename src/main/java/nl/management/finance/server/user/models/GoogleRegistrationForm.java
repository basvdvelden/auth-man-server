package nl.management.finance.server.user.models;

public class GoogleRegistrationForm {
    private String username;
    private String name;
    private String code;

    public GoogleRegistrationForm(String username, String name, String code) {
        this.username = username;
        this.name = name;
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
