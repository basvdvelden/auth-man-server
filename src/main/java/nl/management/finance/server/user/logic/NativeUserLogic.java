package nl.management.finance.server.user.logic;

import nl.management.finance.server.user.exceptions.AuthenticationFailedException;
import nl.management.finance.server.user.models.GoogleRegistrationForm;
import nl.management.finance.server.user.models.NativeUser;
import nl.management.finance.server.user.models.NativeUserAuthForm;
import nl.management.finance.server.user.models.NativeUserRegistrationForm;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class NativeUserLogic {
    private NativeUserRegistrationForm registrationForm;
    private NativeUserAuthForm authForm;
    private PasswordEncoder encoder;

    public NativeUserLogic(NativeUserRegistrationForm form, PasswordEncoder encoder) {
        this.registrationForm = form;
        this.encoder = encoder;
    }

    public NativeUserLogic(NativeUserAuthForm form, PasswordEncoder encoder) {
        this.authForm = form;
        this.encoder = encoder;
    }

    public NativeUser toUser() throws Exception {
        if (registrationForm == null) {
            throw new IllegalAccessException("Wrong logic was constructed for this method!");
        }
        NativeUser nativeUser = new NativeUser();
        UUID uuid = UUID.randomUUID();
        nativeUser.setUuid(uuid);
        nativeUser.setUsername(registrationForm.getUsername());
        nativeUser.setPassword(encoder.encode(registrationForm.getPassword()));
        return nativeUser;
    }

    public void verifyPassword(NativeUser nativeUser) throws AuthenticationFailedException {
        boolean matches = encoder.matches(authForm.getPassword(), nativeUser.getPassword());
        if (!matches) {
            throw new AuthenticationFailedException("Wrong password!");
        }
    }
}
