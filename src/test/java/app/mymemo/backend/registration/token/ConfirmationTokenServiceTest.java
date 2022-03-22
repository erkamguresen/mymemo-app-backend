package app.mymemo.backend.registration.token;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    private ConfirmationTokenService confirmationTokenTestService;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        // given
        confirmationTokenTestService = new ConfirmationTokenService(
                confirmationTokenRepository
        );

        String email = "admin@mymemo.app";

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
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void itCanSaveConfirmationToken() {
        //given
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20),
                this.appUser
        );


        //when
        confirmationTokenTestService.saveConfirmationToken(confirmationToken);

        //then
        verify(confirmationTokenRepository).save(confirmationToken);
    }

    @Test
    void itCanGetAToken() {
        //given
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20),
                this.appUser
        );

        confirmationTokenTestService.saveConfirmationToken(confirmationToken);

        //when
        confirmationTokenTestService.getToken(token);

        //then
        verify(confirmationTokenRepository).findByToken(token);
    }

    @Test
    void itCanSetConfirmedAt() {
        //given
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20),
                this.appUser
        );

        confirmationTokenTestService.saveConfirmationToken(confirmationToken);

        //when
        confirmationTokenTestService.setConfirmedAt(confirmationToken);

        //then
        verify(confirmationTokenRepository, times(2))
                .save(confirmationToken);
    }
}