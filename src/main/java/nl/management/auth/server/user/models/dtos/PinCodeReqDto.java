package nl.management.auth.server.user.models.dtos;

import nl.management.auth.server.exceptions.InvalidPinCodeException;

public class PinCodeReqDto {
    private String pin;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void validate() throws InvalidPinCodeException {
        if (pin == null || !pin.matches("^\\d{5}$")) {
            throw new InvalidPinCodeException("Pin code did not match required pattern or was null!");
        }
    }
}
