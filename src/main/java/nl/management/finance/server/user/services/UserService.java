package nl.management.finance.server.user.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import nl.management.finance.server.common.exceptions.EntityNullException;
import nl.management.finance.server.common.exceptions.InternalServerErrorException;
import nl.management.finance.server.common.jwt.exceptions.RefreshTokenDoesNotExistForGivenUUIDException;
import nl.management.finance.server.common.jwt.AccessTokenService;
import nl.management.finance.server.common.jwt.RefreshTokenService;
import nl.management.finance.server.common.jwt.models.AccessTokenValue;
import nl.management.finance.server.common.jwt.models.RefreshToken;
import nl.management.finance.server.common.jwt.models.RefreshTokenValue;
import nl.management.finance.server.user.dao.GoogleUserRepository;
import nl.management.finance.server.user.dao.NativeUserRepository;
import nl.management.finance.server.user.exceptions.AuthenticationFailedException;
import nl.management.finance.server.user.exceptions.FormInvalidException;
import nl.management.finance.server.user.exceptions.UsernameExistsException;
import nl.management.finance.server.user.logic.GoogleUserLogic;
import nl.management.finance.server.user.logic.NativeUserLogic;
import nl.management.finance.server.user.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final NativeUserRepository nativeUserRepository;
    private final GoogleUserRepository googleUserRepository;
    private final PasswordEncoder encoder;

    private RefreshTokenService refreshTokenService;
    private AccessTokenService accessTokenService;

    @Autowired
    public UserService(NativeUserRepository nativeUserRepository, GoogleUserRepository googleUserRepository, PasswordEncoder encoder, RefreshTokenService refreshTokenService, AccessTokenService accessTokenService) {
        this.nativeUserRepository = nativeUserRepository;
        this.googleUserRepository = googleUserRepository;
        this.encoder = encoder;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(NativeUserRegistrationForm form) throws Exception {
        form.validate();
        verifyUsername(form.getUsername());

        NativeUserLogic logic = new NativeUserLogic(form, encoder);
        NativeUser nativeUser = logic.toUser();

        try {
            nativeUserRepository.save(nativeUser);
        } catch (Exception e) {
            throw new InternalServerErrorException(String.format(
                    "Could not register user %s. stacktrace: %s",
                    nativeUser.getUsername(),
                    Arrays.toString(e.getStackTrace())));
        }
    }

    public AuthenticateResponse authenticate(NativeUserAuthForm form) throws
            AuthenticationFailedException,
            InternalServerErrorException,
            FormInvalidException,
            EntityNullException {

        form.validate();

        NativeUser nativeUser = nativeUserRepository.findByUsername(form.getUsername());
        if (nativeUser != null) {
            NativeUserLogic logic = new NativeUserLogic(form, encoder);
            logic.verifyPassword(nativeUser);
            logoutIfLoggedIn(nativeUser);

            AccessTokenValue accessTokenValue = accessTokenService.createAccessToken(nativeUser);
            RefreshTokenValue refreshTokenValue = refreshTokenService.createRefreshTokenValue();
            RefreshToken refreshToken = refreshTokenService.toEntity(nativeUser, refreshTokenValue);
            refreshTokenService.save(refreshToken);

            return new AuthenticateResponse(refreshTokenValue, accessTokenValue);
        } else {
            throw new AuthenticationFailedException("Username not found!");
        }
    }

    public AuthenticateResponse authenticate(GoogleUserAuthForm form) throws
            AuthenticationFailedException,
            InternalServerErrorException,
            FormInvalidException,
            EntityNullException, GeneralSecurityException, IOException {

        form.validate();
        GoogleUser googleUser = googleUserRepository.findByUsername(form.getUsername());
        if (googleUser == null) {
            register(form);
            googleUser = googleUserRepository.findByUsername(form.getUsername());
        } else {
            logoutIfLoggedIn(googleUser);
        }
        AccessTokenValue accessTokenValue = accessTokenService.createAccessToken(googleUser);
        RefreshTokenValue refreshTokenValue = refreshTokenService.createRefreshTokenValue();
        RefreshToken refreshToken = refreshTokenService.toEntity(googleUser, refreshTokenValue);
        refreshTokenService.save(refreshToken);

        return new AuthenticateResponse(refreshTokenValue, accessTokenValue);
    }

    private void register(GoogleUserAuthForm form) throws AuthenticationFailedException, GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyIdToken(form.getIdToken());

        GoogleIdToken.Payload payload = idToken.getPayload();
        // TODO: toForm should be in logic
        GoogleRegistrationForm registrationForm = toForm(payload);
        GoogleUserLogic logic = new GoogleUserLogic(registrationForm);
        GoogleUser user = logic.toUser();
        googleUserRepository.save(user);
    }

    private GoogleRegistrationForm toForm(GoogleIdToken.Payload payload) {
        String code = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        return new GoogleRegistrationForm(email, name, code);
    }

    // TODO: put idToken in separate class.
    // TODO: put property file name in constants class.
    // TODO: make google verifier a bean.
    // TODO: change name: code to: google user id.
    private GoogleIdToken verifyIdToken(String idToken) throws IOException, GeneralSecurityException, AuthenticationFailedException {
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("google.properties")));
        String clientId = properties.getProperty("client-id");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new AuthenticationFailedException("invalid google id token.");
        }
        return googleIdToken;
    }

    private void logoutIfLoggedIn(User user) {
        try {
            RefreshToken refreshToken = refreshTokenService.getRefreshToken(user.getUuid());

            refreshTokenService.delete(refreshToken);
        } catch (RefreshTokenDoesNotExistForGivenUUIDException e) {
            return;
        }
    }

    private void verifyUsername(String username) throws UsernameExistsException {
        NativeUser nativeUser = nativeUserRepository.findByUsername(username);
        if (nativeUser != null) {
            throw new UsernameExistsException(String.format("Username %s already exists!", username));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
