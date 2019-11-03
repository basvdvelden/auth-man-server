package nl.management.finance.server.common.jwt.models;

public class AccessTokenValue {
    private String value;

    public AccessTokenValue(String value) {
        setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
