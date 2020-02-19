package nl.management.auth.server.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import nl.management.auth.server.exceptions.AuthenticationFailedException;
import nl.management.auth.server.exceptions.InvalidRefreshTokenException;
import nl.management.auth.server.exceptions.RefreshTokenStolenException;
import nl.management.auth.server.exceptions.UsernameExistsException;
import nl.management.auth.server.user.dao.GoogleUserRepository;
import nl.management.auth.server.user.dao.NativeUserRepository;
import nl.management.auth.server.user.dao.UserRepository;
import nl.management.auth.server.user.jwt.TokenService;
import nl.management.auth.server.user.models.dtos.*;
import nl.management.auth.server.user.models.entities.GoogleUser;
import nl.management.auth.server.user.models.entities.NativeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final String GOOGLE_PROPERTIES = "google.properties";

    private final UserRepository<NativeUser, UUID> nativeUserRepository;
    private final UserRepository<GoogleUser, UUID> googleUserRepository;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    @Autowired
    public UserService(
            NativeUserRepository nativeUserRepository,
            GoogleUserRepository googleUserRepository,
            PasswordEncoder encoder,
            TokenService tokenService
    ) {
        this.nativeUserRepository = nativeUserRepository;
        this.googleUserRepository = googleUserRepository;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    void register(@NonNull NativeUserRegistrationReqDto dto) {
        dto.validate();
        verifyUsername(dto.getUsername());
        NativeUser nativeUser = NativeUser.fromRegistrationDto(dto, encoder);
        nativeUserRepository.save(nativeUser);
    }

    @NonNull
    AuthDto authenticate(@NonNull NativeUserAuthReqDto dto) {
        dto.validate();
        NativeUser nativeUser = nativeUserRepository.findByUsername(dto.getUsername());
        if (nativeUser != null) {
            verifyPassword(dto, nativeUser);

            if (tokenService.isLoggedIn(nativeUser.getUuid())) {
                tokenService.logout(nativeUser.getUuid());
            }
            AuthDto authDto = new AuthDto(nativeUser.getName(), nativeUser.getUuid(), nativeUser.isActive(), nativeUser.getUsername());
            return tokenService.authenticate(authDto);
        } else {
            LOG.warn("Authentication failed because username could not be found!");
            throw new AuthenticationFailedException("Username not found!");
        }
    }

    @NonNull
    AuthDto authenticate(@NonNull GoogleUserAuthReqDto form) throws GeneralSecurityException, IOException {
        form.validate();
        GoogleUser googleUser = googleUserRepository.findByUsername(form.getUsername());

        if (googleUser == null) {
            register(form);
            googleUser = googleUserRepository.findByUsername(form.getUsername());
        } else {
            if (tokenService.isLoggedIn(googleUser.getUuid())) {
                tokenService.logout(googleUser.getUuid());
            }
        }
        AuthDto authDto = new AuthDto(googleUser.getName(), googleUser.getUuid(), googleUser.isActive(), googleUser.getUsername());
        return tokenService.authenticate(authDto);
    }

    void logout(@NonNull UUID uuid, @NonNull String accessToken) {
        tokenService.logout(uuid, accessToken);
    }

    @Nullable
    TokenRefreshResDto refreshToken(UUID uuid, String refreshToken, String accessToken) {
        try {
            return tokenService.refresh(accessToken, refreshToken, uuid);
        } catch (RefreshTokenStolenException e) {
            LOG.warn("A refresh token was stolen from user with uuid: {} err msg: {}", uuid.toString(), e.getMessage());
            logout(uuid, accessToken);
            throw new InvalidRefreshTokenException("This refresh token was stolen, user must login again!");
        }
    }

    private void verifyPassword(@NonNull NativeUserAuthReqDto dto, @NonNull NativeUser user) {
        boolean matches = encoder.matches(dto.getPassword(), user.getPassword());
        if (!matches) {
            LOG.warn("Authentication failed because the password was incorrect!");
            throw new AuthenticationFailedException("Wrong password!");
        }
    }

    private void register(GoogleUserAuthReqDto dto) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyIdToken(dto.getIdToken());

        GoogleIdToken.Payload payload = idToken.getPayload();
        GoogleUser user = GoogleUser.fromGooglePayload(payload);
        googleUserRepository.save(user);
    }

    private GoogleIdToken verifyIdToken(String idToken) throws IOException, GeneralSecurityException {
        // TODO: needs work
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(GOOGLE_PROPERTIES)));
        String clientId = properties.getProperty("client-id");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            LOG.warn("Authentication failed because the google id token was null!");
            throw new AuthenticationFailedException("invalid google id token.");
        }
        return googleIdToken;
    }

    private void verifyUsername(String username) {
        NativeUser nativeUser = nativeUserRepository.findByUsername(username);
        if (nativeUser != null) {
            LOG.warn("Username: {} already exists!", username);
            throw new UsernameExistsException(String.format("Username %s already exists!", username));
        }
    }

}
