package app.mymemo.backend.registration.token;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRepository;
import app.mymemo.backend.appuser.AppUserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ConfirmationTokenRepositoryTest {

    @Autowired
    private  ConfirmationTokenRepository tokenTestRepository;

    @Autowired
    private AppUserRepository appUserTestRepository;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        // given
        String email = "jhondoe@gmail.com";

        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);

        AppUser user = new AppUser(
                "John",
                "Doe",
                email,
                "password",
                roles
        );

        this.appUser = user;

        appUserTestRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        tokenTestRepository.deleteAll();
        appUserTestRepository.deleteAll();
    }

    @Test
    void itCanFindConfirmationTokenByToken() {
        // given
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20),
                appUser
        );

        tokenTestRepository.save(confirmationToken);

        //when

        // then
        Optional<ConfirmationToken> actualConfirmationToken =
                tokenTestRepository.findByToken(token);

        assertThat(actualConfirmationToken).isNotEmpty();
        assertThat(actualConfirmationToken.get().getToken())
                .isEqualTo(token);
        assertThat(actualConfirmationToken.get().getConfirmedAt())
                .isNull();
        assertThat(actualConfirmationToken.get().getId())
                .isEqualTo(confirmationToken.getId());
        assertThat(actualConfirmationToken.get().getCreatedAt())
                .isEqualToIgnoringNanos(confirmationToken.getCreatedAt());
        assertThat(actualConfirmationToken.get().getExpiresAt())
                .isEqualToIgnoringNanos(confirmationToken.getExpiresAt());
        assertThat(actualConfirmationToken.get().getAppUser())
                .isEqualTo(confirmationToken.getAppUser());
    }
}