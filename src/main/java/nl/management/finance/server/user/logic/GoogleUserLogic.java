package nl.management.finance.server.user.logic;

import nl.management.finance.server.user.models.GoogleRegistrationForm;
import nl.management.finance.server.user.models.GoogleUser;

import java.util.UUID;

public class GoogleUserLogic {
    private GoogleRegistrationForm registrationForm;

    public GoogleUserLogic(GoogleRegistrationForm registrationForm) {
        this.registrationForm = registrationForm;
    }

    public GoogleUser toUser() {
        GoogleUser user = new GoogleUser();
        user.setCode(registrationForm.getCode());
        user.setName(registrationForm.getName());
        user.setUsername(registrationForm.getUsername());
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);

        return user;
    }
}
