package app.mymemo.backend.appuser;

import app.mymemo.backend.exception.UnauthorizedRequestException;
import app.mymemo.backend.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    private AppUserService testService;

    @BeforeEach
    void setUp() {
        testService = new AppUserService(
                appUserRepository,
                new BCryptPasswordEncoder(),
                confirmationTokenService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void itShouldLoadUserByUsername() {
        // given
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

        appUserRepository.save(user);

        //when
        testService.loadUserByUsername(user.getUsername());

        // then
        verify(appUserRepository).findUserByEmail(user.getEmail());

    }

    @Test
    void itShouldLoadUserById() {
        // given
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

        appUserRepository.save(user);


        //when
        testService.loadUserById(user.getId());

        // then
        verify(appUserRepository).findUserById(user.getId());
    }

    @Test
    void itShouldEnableAppUser() {
        // given
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

        appUserRepository.save(user);

        //when
        testService.enableAppUser(user);

        // then
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void itShouldFindAllUsers() {
    	//when
    	testService.findAllUsers();
    	
    	//then
    	verify(appUserRepository).findAll();

    }

    @Test
    void itShouldAddRoleToUser() {
        // given
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

        String id = UUID.randomUUID().toString();

        user.setId(id);
        appUserRepository.save(user);

        given(appUserRepository.findUserByEmail(anyString()))
                .willReturn(user);

        //when
        testService.addRoleToUser(id, user.getEmail(), "APP_MANAGER_ROLE");

        //then
        verify(appUserRepository, times(2)).save(any());

        assertEquals(user.getRoles().size(),2);
        assertEquals(user.getRoles().get(0),
                Enum.valueOf(AppUserRole.class,"APP_USER_ROLE"));
        assertEquals(user.getRoles().get(1),
                Enum.valueOf(AppUserRole.class,"APP_MANAGER_ROLE"));
    }

    @Test
    void itShouldNotAddRoleToUserWhenItAlreadyHas() {
        // given
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

        String id = UUID.randomUUID().toString();

        user.setId(id);
        appUserRepository.save(user);

        given(appUserRepository.findUserByEmail(anyString()))
                .willReturn(user);

        //when
        testService.addRoleToUser(id, user.getEmail(), "APP_USER_ROLE");

        //then
        verify(appUserRepository, times(1)).save(any());

        assertEquals(user.getRoles().size(),1);
        assertEquals(user.getRoles().get(0),
                Enum.valueOf(AppUserRole.class,"APP_USER_ROLE"));
    }

    @Test
    void itShouldThrowUnauthorizedRequestExceptionAddRoleToUser() {
        // given
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

        String id = UUID.randomUUID().toString();

        user.setId(id);
        appUserRepository.save(user);

        given(appUserRepository.findUserByEmail(anyString()))
                .willReturn(user);


        //then
        assertThatThrownBy(() -> testService.addRoleToUser(
                "UnauthorizedId",
                user.getEmail(),
                "APP_MANAGER_ROLE"))
                .isInstanceOf(UnauthorizedRequestException.class);

        verify(appUserRepository, times(1)).save(any());

        assertEquals(user.getRoles().size(),1);
        assertEquals(user.getRoles().get(0),
                Enum.valueOf(AppUserRole.class,"APP_USER_ROLE"));
    }

    @Test
    void itShouldUpdateUser() {
        // given
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
        String id = UUID.randomUUID().toString();
        user.setId(id);

        appUserRepository.save(user);

        //when
        given(appUserRepository.findUserById(id)).willReturn(user);
        //TODO throw test
//         user.setId("it should not update the id"); //throw

        roles = new ArrayList<>();
        roles.add(AppUserRole.APP_GUEST_USER_ROLE);

        AppUser newUser = new AppUser(
                "it should update the firstname",
                "it should update the lastname",
                "it should not update the email",
                "it should not update the password",
                roles
        );

        newUser.setId(id);
        newUser.setAccountLocked(true);
        newUser.setAccountEnabled(true);

        testService.updateUser(id, newUser);

        // then
        verify(appUserRepository, times(2)).save(any());

        assertThat(user.getFirstName()).isEqualTo("it should update the firstname");
        assertThat(user.getLastName()).isEqualTo("it should update the lastname");
        assertThat(user.getEmail()).isEqualTo("admin@mymemo.app");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.isAccountLocked()).isTrue();
        assertThat(user.isAccountEnabled()).isTrue();
    }

    @Test
    void itShouldThrowUnauthorizedRequestExceptionUpdateUser() {
        // given
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
        String id = UUID.randomUUID().toString();
        user.setId(id);

        appUserRepository.save(user);

        //when

        roles = new ArrayList<>();
        roles.add(AppUserRole.APP_GUEST_USER_ROLE);

        AppUser newUser = new AppUser(
                "it should update the firstname",
                "it should update the lastname",
                "it should not update the email",
                "it should not update the password",
                roles
        );

        newUser.setId(id);
        newUser.setAccountLocked(true);
        newUser.setAccountEnabled(true);

        // then
        assertThatThrownBy(() -> testService.updateUser("UnauthorizedId", newUser))
                .isInstanceOf(UnauthorizedRequestException.class);
        verify(appUserRepository, times(1)).save(any());

        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("admin@mymemo.app");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.isAccountLocked()).isFalse();
        assertThat(user.isAccountEnabled()).isFalse();
    }

    @Test
    void itShouldSignUpUser() {
        // given
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

        //when
        testService.signUpUser(user);

        // then
        ArgumentCaptor<AppUser> userArgumentCaptor =
                ArgumentCaptor.forClass(AppUser.class);

        verify(appUserRepository).save(userArgumentCaptor.capture());

        AppUser capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }
}