package nl.management.finance.server.common.jwt.models;

public class RefreshTokenValue {
    private String value;

    public RefreshTokenValue(String value) {
        setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value.length() != 60) {
            throw new IllegalArgumentException("refresh token value length is not 60!");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
