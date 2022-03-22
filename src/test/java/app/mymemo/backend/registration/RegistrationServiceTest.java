package app.mymemo.backend.registration;

import app.mymemo.backend.appuser.AppUserRepository;
import app.mymemo.backend.exception.BadRequestException;
import app.mymemo.backend.registration.token.ConfirmationToken;
import app.mymemo.backend.registration.token.ConfirmationTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ConfirmationTokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        appUserRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    void itCanRegisterAProperRegistrationRequest() {
        // given
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "admin@mymemo.app",
                "password"
        );

        //when
        String result = registrationService.register(registrationRequest);

        //then
        List<ConfirmationToken> tokens
                = (List<ConfirmationToken>) tokenRepository.findAll();

        assertThat(result).isEqualTo(tokens.get(0).getToken());
    }

    @Test
    void itThrowsABadRequestExceptionWhenEmailIsNotValid() {
        // given
        RegistrationRequest registrationRequest1 = new RegistrationRequest(
                "John",
                "Doe",
                "johndoegmail.com", // no @
                "password"
        );

        RegistrationRequest registrationRequest2 = new RegistrationRequest(
                "John",
                "Doe",
                "johndoe@gmail", // no . after @
                "password"
        );

        RegistrationRequest registrationRequest3 = new RegistrationRequest(
                "John",
                "Doe",
                "john@doe@gmail.com", // double @
                "password"
        );

        RegistrationRequest registrationRequest4 = new RegistrationRequest(
                "John",
                "Doe",
                "johndoe@.com", // no domain
                "password"
        );


        //then
        Exception exception = assertThrows(BadRequestException.class, () -> {
            registrationService.register(registrationRequest1);
        });
        exception = assertThrows(BadRequestException.class, () -> {
            registrationService.register(registrationRequest2);
        });
        exception = assertThrows(BadRequestException.class, () -> {
            registrationService.register(registrationRequest3);
        });
        exception = assertThrows(BadRequestException.class, () -> {
            registrationService.register(registrationRequest4);
        });

    }

    @Test
    void itCanConfirmToken() {
        // given
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "admin@mymemo.app",
                "password"
        );

        //when
        String result = registrationService.register(registrationRequest);

        List<ConfirmationToken> tokens
                = (List<ConfirmationToken>) tokenRepository.findAll();

        //then
        assertThat(registrationService.confirmToken(result))
                .isEqualTo("confirmed");
    }

    @Test
    void itThrowsBadRequestExceptionWhenTokenDoesNotExist() {
        // given
        String token = UUID.randomUUID().toString();

        //when

        //then
        Exception exception = assertThrows(BadRequestException.class, () -> {
            registrationService.confirmToken(token);
        });

        assertThat(exception.getMessage())
                .isEqualTo("Token not found");
    }

    @Test
    void itThrowsBadRequestExceptionWhenTokenHasAlreadyConfirmed() {
        // given
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "admin@mymemo.app",
                "password"
        );

        String token = registrationService.register(registrationRequest);

        //when
        registrationService.confirmToken(token);

        //then
        Exception exception = assertThrows(BadRequestException.class, () -> {
            registrationService.confirmToken(token);
        });

        assertThat(exception.getMessage())
                .isEqualTo("Email already confirmed");
    }

    @Test
    void itThrowsBadRequestExceptionWhenTokenExpired() {
        // given
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John",
                "Doe",
                "admin@mymemo.app",
                "password"
        );

        String token = registrationService.register(registrationRequest);

        //when
        Optional<ConfirmationToken> confirmationToken = tokenRepository.findByToken(token);

        confirmationToken.get().setExpiresAt( LocalDateTime.now());

        tokenRepository.save(confirmationToken.get());

        //then
        Exception exception = assertThrows(BadRequestException.class, () -> {
            registrationService.confirmToken(token);
        });

        assertThat(exception.getMessage())
                .isEqualTo("Token expired");
    }
}