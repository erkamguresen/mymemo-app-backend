package app.mymemo.backend;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRepository;
import app.mymemo.backend.appuser.AppUserRole;
import app.mymemo.backend.appuser.AppUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This integration tests uses actual test server not mock service or DAO.
 * This has the advantage of full integration test.
 * Another reason for this that AppUserService.updateUser() is not evoked
 * and the problem is  "@RequestBody AppUser appUser": conversation to AppUser.
 * Mocking is evoked with a new AppUser() but cannot be evoked by the AppUser
 * from @RequestBody.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AppUserLoginTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository userRepository;


    private AppUser firstUser;

    @BeforeEach
    void setUp() {
        String email = "admin@mymemo.app";
        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);
        firstUser = new AppUser(
                "John",
                "Doe",
                email,
                "password",
                roles
        );
        firstUser.setId(UUID.randomUUID().toString());

        email = "admin@mymemo.app";
        roles = new ArrayList<>();
        roles.add(AppUserRole.APP_ADMIN_ROLE);

        firstUser.setAccountEnabled(true);

        userRepository.save(firstUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    void itCanLoginAnUsers() throws Exception {


        mockMvc.perform(post("/api/v1/login")
                        .contentType("application/x-www-form-urlencoded")
                        .content("username=admin@mymemo.app&password=password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


    }

    @Test
    void itCannotLoginAnUsersWhenCredentialsAreInvalid() throws Exception {}

}