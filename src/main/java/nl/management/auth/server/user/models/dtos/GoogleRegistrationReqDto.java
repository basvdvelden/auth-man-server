package nl.management.auth.server.user.models.dtos;

public class GoogleRegistrationReqDto {
    private String username;
    private String name;
    private String code;

    public GoogleRegistrationReqDto(String username, String name, String code) {
        setUsername(username);
        setName(name);
        setCode(code);
    }

    public GoogleRegistrationReqDto() {}

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
