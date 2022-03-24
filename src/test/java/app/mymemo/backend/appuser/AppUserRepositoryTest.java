package app.mymemo.backend.appuser;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataMongoTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository testRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        testRepository.deleteAll();
    }

    @Test
    void itShouldFindUserByEmail() {
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

        testRepository.save(user);

        // then
        AppUser dbUser = testRepository.findUserByEmail(email);
        assertThat(dbUser).isEqualTo(user);
    }

    @Test
    void itShouldFindUserById() {
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

        testRepository.save(user);

        // when
        String id = user.getId();

        // then
        AppUser dbUser = testRepository.findUserById(id);
        assertThat(dbUser).isEqualTo(user);
    }

    @Test
    void itShouldNotFindUserByEmailWhenItDoesNotExist() {
        // given
        String email = "admin@mymemo.app";

        // then
        AppUser dbUser = testRepository.findUserByEmail(email);
        assertThat(dbUser).isNull();
    }

    @Test
    void itShouldNotFindUserByIdWhenItDoesNotExist() {
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

        // when
        String id = user.getId();

        // then
        AppUser dbUser = testRepository.findUserById(id);
        assertThat(dbUser).isNull();
    }
}