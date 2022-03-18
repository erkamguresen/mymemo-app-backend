package app.mymemo.backend.security.login;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRepository;
import app.mymemo.backend.appuser.AppUserRole;
import app.mymemo.backend.appuser.AppUserService;
import app.mymemo.backend.security.JWTTokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This integration tests uses actual test server not mock service or DAO.
 * This has the advantage of full integration test.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AppUserLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private Environment environment;

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

        appUserService.signUpUser(firstUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    void itCanLoginAnUser() throws Exception {
        //given
        AppUser user = (AppUser) appUserService
                .loadUserByUsername(firstUser.getEmail());

        user.setAccountEnabled(true);

        appUserService.updateUser(user.getId(),user);

        //Then
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username","admin@mymemo.app")
                        .param("password","password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.refresh_token").isString());

        final JSONObject[] jwtObject = new JSONObject[1];

        resultActions.andDo(result1 ->
                jwtObject[0] = new JSONObject(result1.getResponse().getContentAsString()));

        DecodedJWT decodedAccessJWT = JWTTokenService.getVerifiedDecodedJWTFromToken(
                jwtObject[0].get("access_token").toString(),
                environment.getProperty("TOKEN_SECRET")
        );

        DecodedJWT decodedRefreshJWT = JWTTokenService.getVerifiedDecodedJWTFromToken(
                jwtObject[0].get("refresh_token").toString(),
                environment.getProperty("TOKEN_SECRET")
        );

        assertThat(decodedAccessJWT.getAlgorithm()).isEqualTo("HS256");
        assertThat(decodedAccessJWT.getSubject()).isEqualTo("admin@mymemo.app");
        assertThat(decodedAccessJWT.getClaim("roles").asList(String.class))
                .contains("APP_USER_ROLE");
        assertThat(decodedAccessJWT.getClaim("userId").asString()).isEqualTo(user.getId());
        // to avoid environment problems use only the extension
        assertThat(decodedAccessJWT.getIssuer()).contains("/api/v1/login");
        assertThat(decodedAccessJWT.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(60*60*1000));

        assertThat(decodedRefreshJWT.getAlgorithm()).isEqualTo("HS512");
        assertThat(decodedRefreshJWT.getSubject()).isEqualTo("admin@mymemo.app");
        // to avoid environment problems use only the extension
        assertThat(decodedRefreshJWT.getIssuer()).contains("/api/v1/login");
        assertThat(decodedRefreshJWT.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(24*60*60*1000));
    }

    @Test
    void itCannotLoginAnUsersWhenCredentialsAreInvalid() throws Exception {
        //given
        AppUser user = (AppUser) appUserService
                .loadUserByUsername(firstUser.getEmail());

        user.setAccountEnabled(true);

        appUserService.updateUser(user.getId(),user);

        //Then
        ResultActions resultActions = mockMvc.perform(
                        post("/api/v1/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .param("username","admin@mymemo.app")
                                .param("password","password2")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}