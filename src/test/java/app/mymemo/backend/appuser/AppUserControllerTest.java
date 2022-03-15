package app.mymemo.backend.appuser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
class AppUserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository userRepository;


    private AppUser firstUser;
    private AppUser secondUser;


    @BeforeEach
    void setUp() {
        String email = "jhondoe@gmail.com";
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

        email = "janedoe@gmail.com";
        roles = new ArrayList<>();
        roles.add(AppUserRole.APP_ADMIN_ROLE);

        secondUser = new AppUser(
                "Jane",
                "Doe",
                email,
                "password",
                roles
        );

        secondUser.setId(UUID.randomUUID().toString());

        userRepository.save(firstUser);
        userRepository.save(secondUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
//    @Disabled
    void itCanGetAllUsers() throws Exception {
        /*setup mock*/
//        List<AppUser> users = new ArrayList<>();
//        users.add(firstUser);
//        users.add(secondUser);
//
//        when(appUserService.findAllUsers()).thenReturn(users);

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
//    @Disabled
    void itCanGetASpecificUser() throws Exception {
        /*Setup mock*/
//        when(appUserService.loadUserById(firstUser.getId())).thenReturn(firstUser);
//        when(appUserService.loadUserById(secondUser.getId())).thenReturn(secondUser);

        mockMvc.perform(get("/api/v1/users/" + firstUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jhondoe@gmail.com"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.roles[0]").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.credentialsNonExpired").value(true))
                .andExpect(jsonPath("$.accountNonExpired").value(true))
                .andExpect(jsonPath("$.accountLocked").value(false))
                .andExpect(jsonPath("$.accountNonLocked").value(true))
                .andExpect(jsonPath("$.accountEnabled").value(false))
                .andExpect(jsonPath("$.authorities[0].authority").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.username").value("jhondoe@gmail.com"));

        mockMvc.perform(get("/api/v1/users/" + secondUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("janedoe@gmail.com"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.roles[0]").value("APP_ADMIN_ROLE"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.credentialsNonExpired").value(true))
                .andExpect(jsonPath("$.accountNonExpired").value(true))
                .andExpect(jsonPath("$.accountLocked").value(false))
                .andExpect(jsonPath("$.accountNonLocked").value(true))
                .andExpect(jsonPath("$.accountEnabled").value(false))
                .andExpect(jsonPath("$.authorities[0].authority").value("APP_ADMIN_ROLE"))
                .andExpect(jsonPath("$.username").value("janedoe@gmail.com"));

//        verify(appUserService, times(2)).loadUserById(anyString());
    }

    @Test
    void itCanUpdateAnExistingUserForAllowedProperties() throws Exception {
        //given
        String url = "/api/v1/users/" + firstUser.getId()+ "/update";

        String content = "{\n" +
                "\"id\": \"" + firstUser.getId()+ "\",\n" +
                "    \"firstName\": \"J.James\",\n" +
                "    \"lastName\": \"Doen\",\n" +
                "    \"email\": \"jjdoen@gmail.com\",\n" +
                "    \"password\": \"password2\",\n" +
                "    \"roles\": [\n" +
                "        \"APP_USER_ROLE\"\n," +
                "        \"APP_ADMIN_ROLE\"\n" +
                "    ],\n" +
                "    \"enabled\": true,\n" +
                "    \"username\": \"jjdoen@gmail.com\"" +
                "}";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("J.James"))
                .andExpect(jsonPath("$.lastName").value("Doen"))
                .andExpect(jsonPath("$.email").value("jhondoe@gmail.com"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.roles[0]").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.credentialsNonExpired").value(true))
                .andExpect(jsonPath("$.accountNonExpired").value(true))
                .andExpect(jsonPath("$.accountLocked").value(false))
                .andExpect(jsonPath("$.accountNonLocked").value(true))
                .andExpect(jsonPath("$.accountEnabled").value(false))
                .andExpect(jsonPath("$.authorities[0].authority").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.username").value("jhondoe@gmail.com"));
    }

    @Test
    void itCanAddRoleToAppUser() throws Exception {
        //given
        String url = "/api/v1/users/" + firstUser.getId()+ "/roles/add-role";

        String content = "{\n" +
                "\"id\": \"" + firstUser.getId()+ "\",\n" +
                "    \"firstName\": \"J.James\",\n" +
                "    \"lastName\": \"Doen\",\n" +
                "    \"email\": \"jjdoen@gmail.com\",\n" +
                "    \"password\": \"password2\",\n" +
                "    \"roleName\": \"APP_ADMIN_ROLE\",\n" +
                "    \"username\": \"jhondoe@gmail.com\"" +
                "}";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}