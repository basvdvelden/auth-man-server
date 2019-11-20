package nl.management.auth.server.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import nl.management.auth.server.exceptions.*;
import nl.management.auth.server.user.dao.GoogleUserRepository;
import nl.management.auth.server.user.dao.NativeUserRepository;
import nl.management.auth.server.user.dao.UserRepository;
import nl.management.auth.server.user.jwt.AccessTokenService;
import nl.management.auth.server.user.jwt.RefreshTokenService;
import nl.management.auth.server.user.models.dtos.*;
import nl.management.auth.server.user.models.entities.GoogleUser;
import nl.management.auth.server.user.models.entities.NativeUser;
import nl.management.auth.server.user.models.entities.User;
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
    private static final java.lang.String GOOGLE_PROPERTIES = "google.properties";

    private final UserRepository<NativeUser, UUID> nativeUserRepository;
    private final UserRepository<GoogleUser, UUID> googleUserRepository;
    private final PasswordEncoder encoder;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    @Autowired
    public UserService(
            NativeUserRepository nativeUserRepository,
            GoogleUserRepository googleUserRepository,
            PasswordEncoder encoder,
            AccessTokenService accessTokenService,
            RefreshTokenService refreshTokenService
    ) {

        this.nativeUserRepository = nativeUserRepository;
        this.googleUserRepository = googleUserRepository;
        this.encoder = encoder;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(@NonNull NativeUserRegistrationReqDto dto) throws Exception {
        dto.validate();
        verifyUsername(dto.getUsername());

        NativeUser nativeUser = NativeUser.fromRegistrationDto(dto, encoder);
        nativeUserRepository.save(nativeUser);
    }

    @Nullable
    public AuthResDto authenticate(@NonNull NativeUserAuthReqDto dto) throws
            AuthenticationFailedException,
            FormInvalidException,
            AccessTokenCreationFailedException {

        dto.validate();

        NativeUser nativeUser = nativeUserRepository.findByUsername(dto.getUsername());
        if (nativeUser != null) {
            verifyPassword(dto, nativeUser);

            boolean loggedIn = isLoggedIn(nativeUser);
            if (loggedIn) {
                return null;
            }

            return refreshTokenService.authenticate(nativeUser);
        } else {
            LOG.warn("Authentication failed because username could not be found!");
            throw new AuthenticationFailedException("Username not found!");
        }
    }

    public AuthResDto authenticate(@NonNull GoogleUserAuthReqDto form) throws
            AuthenticationFailedException,
            FormInvalidException,
            GeneralSecurityException,
            AccessTokenCreationFailedException,
            IOException {

        form.validate();
        GoogleUser googleUser = googleUserRepository.findByUsername(form.getUsername());

        if (googleUser == null) {
            register(form);
            googleUser = googleUserRepository.findByUsername(form.getUsername());
        } else {
            boolean loggedIn = isLoggedIn(googleUser);
            if (loggedIn) {
                return null;
            }
        }
        return refreshTokenService.authenticate(googleUser);
    }

    public void logout(@NonNull UUID uuid, @NonNull String accessToken) throws JWTParsingFailedException {
        try {
            refreshTokenService.deleteForUUID(uuid);
        } catch (RefreshTokenDoesNotExistForGivenUUIDException ignored) {
        }

        accessTokenService.invalidate(accessToken);
    }

    public TokenRefreshResDto refreshToken(UUID uuid, RefreshTokenReqDto dto, String accessToken) throws
            RefreshTokenExpiredException,
            InvalidRefreshTokenException,
            RefreshTokenDoesNotExistForGivenUUIDException,
            InvalidAccessTokenException,
            AccessTokenCreationFailedException,
            JWTParsingFailedException {

        User user = findAnyUser(uuid);
        String newAccessToken = accessTokenService.createAccessToken(user);
        accessTokenService.invalidate(accessToken);
        try {
            return refreshTokenService.refresh(newAccessToken, uuid, dto, accessToken);
        } catch (RefreshTokenStolenException e) {
            LOG.warn("A refresh token was stolen from user with uuid: {} err msg: {}", uuid.toString(), e.getMessage());
            logout(uuid, accessToken);
            throw new InvalidRefreshTokenException("This refresh token was stolen, user must login again!");
        }
    }

    private void verifyPassword(@NonNull NativeUserAuthReqDto dto, @NonNull NativeUser user) throws AuthenticationFailedException {
        boolean matches = encoder.matches(dto.getPassword(), user.getPassword());
        if (!matches) {
            LOG.warn("Authentication failed because the password was incorrect!");
            throw new AuthenticationFailedException("Wrong password!");
        }
    }

    private User findAnyUser(UUID uuid) {
        User user = googleUserRepository.findByUuid(uuid);
        if (user == null) {
            user = nativeUserRepository.findByUuid(uuid);
        }
        return user;
    }

    private void register(GoogleUserAuthReqDto dto) throws AuthenticationFailedException, GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyIdToken(dto.getIdToken());

        GoogleIdToken.Payload payload = idToken.getPayload();
        GoogleUser user = GoogleUser.fromGooglePayload(payload);
        googleUserRepository.save(user);
    }

    private GoogleIdToken verifyIdToken(String idToken) throws IOException, GeneralSecurityException, AuthenticationFailedException {
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

    private boolean isLoggedIn(User user) {
        try {
            refreshTokenService.getRefreshToken(user.getUuid());
            return true;
        } catch (RefreshTokenDoesNotExistForGivenUUIDException ignored) {
            return false;
        }
    }

    private void verifyUsername(String username) throws UsernameExistsException {
        NativeUser nativeUser = nativeUserRepository.findByUsername(username);
        if (nativeUser != null) {
            LOG.warn("Username: {} already exists!", username);
            throw new UsernameExistsException(String.format("Username %s already exists!", username));
        }
    }

}
