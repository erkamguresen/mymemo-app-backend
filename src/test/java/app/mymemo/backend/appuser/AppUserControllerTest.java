package app.mymemo.backend.appuser;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Autowired
    private Environment environment;


    private AppUser firstUser;
    private AppUser secondUser;

    private String firstToken;
    private String secondToken;


    @BeforeEach
    void setUp() {
        String email = "john@mymemo.app";
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

        email = "jane@mymemo.app";
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

        Algorithm algorithm = Algorithm.HMAC256(
                this.environment.getProperty("TOKEN_SECRET"));

        firstToken = JWT.create()
                // use stg unique
                .withSubject(firstUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60*60*1000))
                .withIssuer("https://www.mymemo.app/api/v1/test")
                .withClaim("roles",
                        firstUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .withClaim("userId",firstUser.getId())
                .sign(algorithm);

        secondToken = JWT.create()
                // use stg unique
                .withSubject(secondUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60*60*1000))
                .withIssuer("https://www.mymemo.app/api/v1/test")
                .withClaim("roles",
                        secondUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .withClaim("userId",secondUser.getId())
                .sign(algorithm);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
//    @Disabled
    void itCanGetAllUsers() throws Exception {

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+firstToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john@mymemo.app"))
                .andExpect(jsonPath("$[0].password").value("password"))
                .andExpect(jsonPath("$[0].roles[0]").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$[0].enabled").value(false))
                .andExpect(jsonPath("$[0].credentialsNonExpired").value(true))
                .andExpect(jsonPath("$[0].accountNonExpired").value(true))
                .andExpect(jsonPath("$[0].accountLocked").value(false))
                .andExpect(jsonPath("$[0].accountNonLocked").value(true))
                .andExpect(jsonPath("$[0].accountEnabled").value(false))
                .andExpect(jsonPath("$[0].authorities[0].authority").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$[0].username").value("john@mymemo.app"))

                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].email").value("jane@mymemo.app"))
                .andExpect(jsonPath("$[1].password").value("password"))
                .andExpect(jsonPath("$[1].roles[0]").value("APP_ADMIN_ROLE"))
                .andExpect(jsonPath("$[1].enabled").value(false))
                .andExpect(jsonPath("$[1].credentialsNonExpired").value(true))
                .andExpect(jsonPath("$[1].accountNonExpired").value(true))
                .andExpect(jsonPath("$[1].accountLocked").value(false))
                .andExpect(jsonPath("$[1].accountNonLocked").value(true))
                .andExpect(jsonPath("$[1].accountEnabled").value(false))
                .andExpect(jsonPath("$[1].authorities[0].authority").value("APP_ADMIN_ROLE"))
                .andExpect(jsonPath("$[1].username").value("jane@mymemo.app"));

    }

    @Test
    void itCanGetASpecificUser() throws Exception {

        mockMvc.perform(get("/api/v1/users/" + firstUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+firstToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@mymemo.app"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.roles[0]").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.credentialsNonExpired").value(true))
                .andExpect(jsonPath("$.accountNonExpired").value(true))
                .andExpect(jsonPath("$.accountLocked").value(false))
                .andExpect(jsonPath("$.accountNonLocked").value(true))
                .andExpect(jsonPath("$.accountEnabled").value(false))
                .andExpect(jsonPath("$.authorities[0].authority")
                        .value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.username").value("john@mymemo.app"));
    }

    @Test
    void itThrows403WhenNo_APP_USER_ROLETryToGetASpecificUser() throws Exception {
        // this route is for users himself, admins reach user info from another link so
        mockMvc.perform(get("/api/v1/users/" + secondUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void itThrows403WhenInvalidTokenToGetASpecificUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + firstUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void itCanUpdateAnExistingUserForAllowedProperties() throws Exception {
        //given
        String url = "/api/v1/users/" + firstUser.getId()+ "/update";

        String content = "{\n" +
                "\"id\": \"" + firstUser.getId()+ "\",\n" +
                "    \"firstName\": \"J.James\",\n" +
                "    \"lastName\": \"Doen\",\n" +
                "    \"email\": \"admin@mymemo.app\",\n" +
                "    \"password\": \"password2\",\n" +
                "    \"roles\": [\n" +
                "        \"APP_USER_ROLE\"\n," +
                "        \"APP_ADMIN_ROLE\"\n" +
                "    ],\n" +
                "    \"enabled\": true,\n" +
                "    \"username\": \"admin@mymemo.app\"" +
                "}";

        mockMvc.perform(post(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+firstToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("J.James"))
                .andExpect(jsonPath("$.lastName").value("Doen"))
                .andExpect(jsonPath("$.email").value("john@mymemo.app"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.roles[0]").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.credentialsNonExpired").value(true))
                .andExpect(jsonPath("$.accountNonExpired").value(true))
                .andExpect(jsonPath("$.accountLocked").value(false))
                .andExpect(jsonPath("$.accountNonLocked").value(true))
                .andExpect(jsonPath("$.accountEnabled").value(false))
                .andExpect(jsonPath("$.authorities[0].authority").value("APP_USER_ROLE"))
                .andExpect(jsonPath("$.username").value("john@mymemo.app"));
    }

    @Test
    void itCanAddRoleToAppUser() throws Exception {
        //given
        String url = "/api/v1/users/" + firstUser.getId()+ "/roles/add-role";

        String content = "{\n" +
                "\"id\": \"" + firstUser.getId()+ "\",\n" +
                "    \"firstName\": \"J.James\",\n" +
                "    \"lastName\": \"Doen\",\n" +
                "    \"email\": \"john@mymemo.app\",\n" +
                "    \"password\": \"password2\",\n" +
                "    \"roleName\": \"APP_ADMIN_ROLE\",\n" +
                "    \"username\": \"john@mymemo.app\"" +
                "}";

        mockMvc.perform(post(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+firstToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}