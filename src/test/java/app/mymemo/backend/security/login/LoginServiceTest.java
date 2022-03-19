package app.mymemo.backend.security.login;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRepository;
import app.mymemo.backend.appuser.AppUserRole;
import app.mymemo.backend.appuser.AppUserService;
import app.mymemo.backend.security.JWTTokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LoginServiceTest {

    @Autowired
    private LoginService loginService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private JWTTokenService jwtTokenService;
    @Autowired
    private Environment environment;

    private String TOKEN_SECRET;
    private String refreshToken1, refreshToken2, accessToken;

    private AppUser user;

    @BeforeEach
    void setUp() {

        TOKEN_SECRET = environment.getProperty("TOKEN_SECRET");

        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);
        roles.add(AppUserRole.APP_PREMIUM_USER_ROLE);

        user = new AppUser(
                "jhon",
                "doe",
                "jhondoe@mymemo.app",
                "password",
                roles
        );

        appUserRepository.save(user);

        user = appUserRepository.findUserByEmail("jhondoe@mymemo.app");

        accessToken = JWT.create()
                // use stg unique
                .withSubject("jhondoe@mymemo.app")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60*60*1000))
                .withIssuer("https://www.mymemo.app/api/v1/test")
                .withClaim("roles",
                        "[\"APP_USER_ROLE\"]")
                .withClaim("userId",user.getId())
                .sign(Algorithm.HMAC256(TOKEN_SECRET));

        refreshToken1 = JWT.create()
                // use stg unique
                .withSubject("jhondoe@mymemo.app")
                .withExpiresAt(new Date(System.currentTimeMillis() + 24*60*60*1000))
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
        appUserRepository.deleteAll();
    }

    @Test
    void canRefreshAccessTokenByAValidRefreshToken() throws IOException, JSONException {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest(
                "post",
                "https://www.mymemo.app/api/v1/login/token/refresh");
        request.addHeader("Authorization","Bearer " + refreshToken1 );

        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        loginService.refreshAccessTokenByRefreshToken(request, response);

        //then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo("application/json");

        String responseContent = response.getContentAsString();

        JSONObject responseObject = new JSONObject(responseContent);

        assertThat(responseObject.get("refresh_token")).isEqualTo(refreshToken1);

        DecodedJWT decodedJWT1 = jwtTokenService.getVerifiedDecodedJWTFromToken(
                responseObject.get("access_token").toString());

        assertThat(decodedJWT1.getAlgorithm()).isEqualTo("HS256");
        assertThat(decodedJWT1.getSubject()).isEqualTo("jhondoe@mymemo.app");
        assertThat(decodedJWT1.getClaim("roles").toString())
                .contains("APP_USER_ROLE");
        assertThat(decodedJWT1.getClaim("roles").toString())
                .contains("APP_PREMIUM_USER_ROLE");

        assertThat(decodedJWT1.getClaim("userId").asString())
                .isEqualTo(user.getId());
        // to avoid environment problems use only the extension
        assertThat(decodedJWT1.getIssuer()).contains("api/v1/login/token/refresh");
        assertThat(decodedJWT1.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(60*60*1000));
    }

    @Test
    void throwsExceptionWhenAnInvalidRefreshTokenIsSend() throws IOException, JSONException {
        //TODO expired, access token, wrongrefresh
        fail("not yet implemented.");
    }
}