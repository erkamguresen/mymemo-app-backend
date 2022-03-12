package app.mymemo.backend.appuser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.*;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AppUserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AppUserService appUserServiceMock;


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void itShouldGetUsers() throws Exception {
        /*setup mock*/
        List<AppUser> users = new ArrayList<>();

        String email = "jhondoe@gmail.com";
        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);
        AppUser firstUser = new AppUser(
                "John",
                "Doe",
                email,
                "password",
                roles
        );
        users.add(firstUser);

        email = "janedoe@gmail.com";
        roles = new ArrayList<>();
        roles.add(AppUserRole.APP_ADMIN_ROLE);

        AppUser secondUser = new AppUser(
                "Jane",
                "Doe",
                email,
                "password",
                roles
        );
        users.add(secondUser);

        when(appUserServiceMock.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("jhondoe@gmail.com"))
                .andExpect(jsonPath("$[0].password").value("password"))
                .andExpect(jsonPath("$[0].roles[0]").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$[0].enabled").value(false))
                .andExpect(jsonPath("$[0].credentialsNonExpired").value(true))
                .andExpect(jsonPath("$[0].accountNonExpired").value(true))
                .andExpect(jsonPath("$[0].accountLocked").value(false))
                .andExpect(jsonPath("$[0].accountNonLocked").value(true))
                .andExpect(jsonPath("$[0].accountEnabled").value(false))
                .andExpect(jsonPath("$[0].authorities[0].authority").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$[0].username").value("jhondoe@gmail.com"))

                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].email").value("janedoe@gmail.com"))
                .andExpect(jsonPath("$[1].password").value("password"))
                .andExpect(jsonPath("$[1].roles[0]").value("APP_ADMIN_ROLE"))
                .andExpect(jsonPath("$[1].enabled").value(false))
                .andExpect(jsonPath("$[1].credentialsNonExpired").value(true))
                .andExpect(jsonPath("$[1].accountNonExpired").value(true))
                .andExpect(jsonPath("$[1].accountLocked").value(false))
                .andExpect(jsonPath("$[1].accountNonLocked").value(true))
                .andExpect(jsonPath("$[1].accountEnabled").value(false))
                .andExpect(jsonPath("$[1].authorities[0].authority").value("APP_ADMIN_ROLE"))
                .andExpect(jsonPath("$[1].username").value("janedoe@gmail.com"));

    }

    @Test
    void itShouldGetUser()  {

    }

    @Test
    void updateUser() {

    }

    @Test
    void addRoleToAppUser() {
    }
}