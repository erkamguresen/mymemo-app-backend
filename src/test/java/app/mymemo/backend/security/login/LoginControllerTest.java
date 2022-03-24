package app.mymemo.backend.security.login;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRepository;
import app.mymemo.backend.appuser.AppUserRole;
import app.mymemo.backend.security.JWTTokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LoginControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private JWTTokenService jwtTokenService;
    @Autowired
    private Environment environment;

    private AppUser firstUser;
    private AppUser savedUser;
    private String accessToken;
    private String refreshToken;
    private String refreshToken1_expired;
    private String refreshToken2;



    @BeforeEach
    void setUp() {
        String email = "johndoe@mymemo.app";
        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);

        firstUser = new AppUser(
                "John",
                "Doe",
                email,
                "password",
                roles
        );

        firstUser.setAccountEnabled(true);

        userRepository.save(firstUser);

        savedUser = userRepository.findUserByEmail(firstUser.getEmail());

        accessToken = jwtTokenService.createAccessToken(
                savedUser,
                "/api/v*/test"
        );

        refreshToken = jwtTokenService.createRefreshToken(
                savedUser,
                "/api/v*/test"
        );

        String TOKEN_SECRET = environment.getProperty("TOKEN_SECRET");

        refreshToken1_expired = JWT.create()
                // use stg unique
                .withSubject("johndoe@mymemo.app")
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000))
                .withIssuer("https://www.mymemo.app/api/v1/test")
                .withClaim("token-type","refresh token")
                .sign(Algorithm.HMAC512(TOKEN_SECRET));

        refreshToken2 = JWT.create()
                // use stg unique
                .withSubject("jane@mymemo.app")
                .withExpiresAt(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .withIssuer("https://www.mymemo.app/api/v1/test")
                .withClaim("token-type","refresh token")
                .sign(Algorithm.HMAC512(TOKEN_SECRET));
    }

    @AfterEach
    void tearDown() {

        userRepository.deleteAll();
    }

    /*
    * In this test Mockmcv has a null pointer exception for an array and
    * the request does not even send a get to the controller. But it works
    * for invalid tokens and goes to the url, and you can debug from starting
    * the controller.
    *
    * For this reason old school RestTemplate method is
    * used to make a get request in this test.
    */
    @Test
    void canRefreshAccessTokenWithARefreshToken() throws Exception {
        // Given
        String url = "http://localhost:" + port + "/api/v1/login/token/refresh";

        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `BearerAuth` header
        headers.setBearerAuth(refreshToken);

        // build the request
        HttpEntity request = new HttpEntity(headers);

        // when (make an HTTP GET request with headers)
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class,
                0
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        JSONObject responseObject = new JSONObject(response.getBody());
        String returnedRefreshToken = responseObject.getString("refresh_token");

        assertThat(returnedRefreshToken).isEqualTo(refreshToken);

        String newAccessToken = responseObject.getString("access_token");

        DecodedJWT decodedNewAccessToken =
                jwtTokenService.getVerifiedDecodedJWTFromToken(newAccessToken);

        assertThat(decodedNewAccessToken.getSubject())
                .isEqualTo("johndoe@mymemo.app");
        assertThat(decodedNewAccessToken.getAlgorithm())
                .isEqualTo("HS256");
        assertThat(decodedNewAccessToken.getClaim("roles").toString())
                .contains("APP_USER_ROLE");
        assertThat(decodedNewAccessToken.getClaim("userId").asString())
                .isEqualTo(savedUser.getId());
        // to avoid environment problems use only the extension
        assertThat(decodedNewAccessToken.getIssuer())
                .contains("/api/v1/login/token/refresh");
        assertThat(decodedNewAccessToken.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(60*60*1000));
    }

    @Test
    void throwsExceptionWhenAnAccessTokenIsSend() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/login/token/refresh";

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(
                                "{\"error_message\":\"Refresh Token is missing\"}"));
    }

    @Test
    void throwsExceptionWhenAnExpiredRefreshTokenIsSend() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/login/token/refresh";

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .header(
                        HttpHeaders.AUTHORIZATION,
//                                    "Bearer " + refreshToken)
                        "Bearer " + refreshToken1_expired)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(
                        "$.error_message",
                        containsString("The Token has expired on")));
    }

    @Test
    void throwsExceptionWhenInvalidRefreshTokenIsSend() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/login/token/refresh";

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                .header(
                        HttpHeaders.AUTHORIZATION,
//                                    "Bearer " + refreshToken)
                        "Bearer " + refreshToken2)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}