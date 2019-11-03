package nl.management.finance.server.user.services;

import nl.management.finance.server.common.exceptions.EntityNullException;
import nl.management.finance.server.common.exceptions.InternalServerErrorException;
import nl.management.finance.server.common.jwt.exceptions.RefreshTokenDoesNotExistForGivenUUIDException;
import nl.management.finance.server.common.jwt.AccessTokenService;
import nl.management.finance.server.common.jwt.RefreshTokenService;
import nl.management.finance.server.common.jwt.models.AccessTokenValue;
import nl.management.finance.server.common.jwt.models.RefreshToken;
import nl.management.finance.server.common.jwt.models.RefreshTokenValue;
import nl.management.finance.server.user.dao.NativeUserRepository;
import nl.management.finance.server.user.exceptions.AuthenticationFailedException;
import nl.management.finance.server.user.exceptions.FormInvalidException;
import nl.management.finance.server.user.exceptions.UsernameExistsException;
import nl.management.finance.server.user.logic.UserLogic;
import nl.management.finance.server.user.models.AuthenticateResponse;
import nl.management.finance.server.user.models.NativeUser;
import nl.management.finance.server.user.models.NativeUserAuthForm;
import nl.management.finance.server.user.models.NativeUserRegistrationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class UserService {

    private final NativeUserRepository nativeUserRepository;
    private final PasswordEncoder encoder;

    private RefreshTokenService refreshTokenService;
    private AccessTokenService accessTokenService;

    @Autowired
    public UserService(NativeUserRepository nativeUserRepository, PasswordEncoder encoder, RefreshTokenService refreshTokenService, AccessTokenService accessTokenService) {
        this.nativeUserRepository = nativeUserRepository;
        this.encoder = encoder;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    private void verifyUsername(String username) throws UsernameExistsException {
        NativeUser nativeUser = nativeUserRepository.findByUsername(username);
        if (nativeUser != null) {
            throw new UsernameExistsException(String.format("Username %s already exists!", username));
        }
    }

    public void register(NativeUserRegistrationForm form) throws Exception {
        form.validate();
        verifyUsername(form.getUsername());

        UserLogic logic = new UserLogic(form, encoder);
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

        NativeUser nativeUser;
        try {
            nativeUser = nativeUserRepository.findByUsername(form.getUsername());
        } catch (Exception e) {
            throw new InternalServerErrorException(String.format(
                    "Could not find user by username: %s stacktrace: %s",
                    form.getUsername(),
                    Arrays.toString(e.getStackTrace())));
        }
        if (nativeUser != null) {
            UserLogic logic = new UserLogic(form, encoder);
            logic.verifyPassword(nativeUser);
            logoutIfLoggedIn(nativeUser.getUuid());

            AccessTokenValue accessTokenValue = accessTokenService.getAccessToken(nativeUser.getUuid());
            RefreshTokenValue refreshTokenValue = refreshTokenService.getRefreshTokenValue();
            RefreshToken refreshToken = refreshTokenService.toEntity(nativeUser.getUuid(), refreshTokenValue);
            refreshToken = refreshTokenService.save(refreshToken);

            return new AuthenticateResponse(refreshTokenValue, accessTokenValue);
        } else {
            throw new AuthenticationFailedException("Username not found!");
        }
    }

    private void logoutIfLoggedIn(UUID uuid) {
        try {
            System.out.println(uuid);
            RefreshToken refreshToken = refreshTokenService.getRefreshToken(uuid);

            refreshTokenService.delete(refreshToken);
        } catch (RefreshTokenDoesNotExistForGivenUUIDException e) {
            return;
        }
    }

}
