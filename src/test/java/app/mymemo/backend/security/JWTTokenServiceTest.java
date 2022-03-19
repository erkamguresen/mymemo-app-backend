package app.mymemo.backend.security;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserRole;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JWTTokenServiceTest {

    private final String TOKEN_SECRET = "TOKEN_SECRET";
    private String token1, token2;

    @Autowired
    private JWTTokenService jwtTokenService;
    @Autowired
    Environment environment;

    @BeforeEach
    void setUp() {

        token1 = JWT.create()
                // use stg unique
                .withSubject("johndoe@mymemo.app")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60*60*1000))
                .withIssuer("https://www.mymemo.app/api/v1/test")
                .withClaim("roles",
                        "[\"APP_USER_ROLE\"]")
                .withClaim("userId","623452c50f23c010c7bc3b80")
                .sign(Algorithm.HMAC256(TOKEN_SECRET));

        token2 = JWT.create()
                // use stg unique
                .withSubject("janedoe@mymemo.app")
                .withExpiresAt(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .withIssuer("https://www.mymemo.app/api/v1/test")
                .withClaim("token-type","refresh token")
                // here I used a more complex algorithm
                .sign(Algorithm.HMAC512(TOKEN_SECRET));
    }


    @Test
    void itShouldGetVerifiedDecodedJWTFromHeader() {
        //given
        String authorizationTokenHeader1 = "Bearer "+token1;
        String authorizationTokenHeader2 = "Bearer "+token2;

        //when
        DecodedJWT decodedJWT1 = JWTTokenService
                .getVerifiedDecodedJWTFromHeader(
                        authorizationTokenHeader1,
                        TOKEN_SECRET
                );

        DecodedJWT decodedJWT2 = JWTTokenService
                .getVerifiedDecodedJWTFromHeader(
                        authorizationTokenHeader2,
                        TOKEN_SECRET
                );

        //then
        assertThat(decodedJWT1.getAlgorithm()).isEqualTo("HS256");
        assertThat(decodedJWT1.getSubject()).isEqualTo("johndoe@mymemo.app");
        assertThat(decodedJWT1.getClaim("roles").toString())
                .contains("APP_USER_ROLE");
        assertThat(decodedJWT1.getClaim("userId").asString()).isEqualTo("623452c50f23c010c7bc3b80");
        // to avoid environment problems use only the extension
        assertThat(decodedJWT1.getIssuer()).contains("https://www.mymemo.app/api/v1/test");
        assertThat(decodedJWT1.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(60*60*1000));

        assertThat(decodedJWT2.getAlgorithm()).isEqualTo("HS512");
        assertThat(decodedJWT2.getSubject()).isEqualTo("janedoe@mymemo.app");
        // to avoid environment problems use only the extension
        assertThat(decodedJWT2.getIssuer()).contains("https://www.mymemo.app/api/v1/test");
        assertThat(decodedJWT2.getClaim("token-type").asString())
                .isEqualTo("refresh token");
        assertThat(decodedJWT2.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(24*60*60*1000));
    }

    @Test
    void itShouldGetVerifiedDecodedJWTFromToken() {
        //given

        //when
        DecodedJWT decodedJWT1 = JWTTokenService
                .getVerifiedDecodedJWTFromToken(
                        token1,
                        TOKEN_SECRET
                );

        DecodedJWT decodedJWT2 = JWTTokenService
                .getVerifiedDecodedJWTFromToken(
                        token2,
                        TOKEN_SECRET
                );

        //then
        assertThat(decodedJWT1.getAlgorithm()).isEqualTo("HS256");
        assertThat(decodedJWT1.getSubject()).isEqualTo("johndoe@mymemo.app");
        assertThat(decodedJWT1.getClaim("roles").toString())
                .contains("APP_USER_ROLE");
        assertThat(decodedJWT1.getClaim("userId").asString()).isEqualTo("623452c50f23c010c7bc3b80");
        // to avoid environment problems use only the extension
        assertThat(decodedJWT1.getIssuer()).contains("https://www.mymemo.app/api/v1/test");
        assertThat(decodedJWT1.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(60*60*1000));

        assertThat(decodedJWT2.getAlgorithm()).isEqualTo("HS512");
        assertThat(decodedJWT2.getSubject()).isEqualTo("janedoe@mymemo.app");
        // to avoid environment problems use only the extension
        assertThat(decodedJWT2.getIssuer()).contains("https://www.mymemo.app/api/v1/test");
        assertThat(decodedJWT2.getClaim("token-type").asString())
                .isEqualTo("refresh token");
        assertThat(decodedJWT2.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(24*60*60*1000));
    }

    @Test
    void itShouldCreateAccessToken() {
        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);
        roles.add(AppUserRole.APP_ADMIN_ROLE);

        String issuedBy = "/api/v*/registerTheUser";

        //given
        AppUser user = new AppUser(
                "john",
                "doe",
                "johndoe@mymemo.app",
                "topPassword",
                roles
        );

        user.setId(UUID.randomUUID().toString());

        //when
        String token = jwtTokenService.createAccessToken(user, issuedBy);

        //then
        JWTVerifier jwtVerifier = JWT.require(Algorithm
                .HMAC256(environment.getProperty("TOKEN_SECRET"))).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        assertThat(decodedJWT.getAlgorithm()).isEqualTo("HS256");
        assertThat(decodedJWT.getSubject()).isEqualTo("johndoe@mymemo.app");
        assertThat(decodedJWT.getClaim("roles").toString())
                .contains("APP_USER_ROLE")
                .contains("APP_ADMIN_ROLE");
        assertThat(decodedJWT.getClaim("userId").asString())
                .isEqualTo(user.getId());
        // to avoid environment problems use only the extension
        assertThat(decodedJWT.getIssuer()).contains("/api/v*/registerTheUser");
        assertThat(decodedJWT.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(60*60*1000));
    }

    @Test
    void itShouldCreateRefreshToken() {
        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);
        roles.add(AppUserRole.APP_ADMIN_ROLE);

        String issuedBy = "/api/v*/registerTheUser";

        //given
        AppUser user = new AppUser(
                "john",
                "doe",
                "johndoe@mymemo.app",
                "topPassword",
                roles
        );

        user.setId(UUID.randomUUID().toString());

        //when
        String token = jwtTokenService.createRefreshToken(user, issuedBy);

        //then
        JWTVerifier jwtVerifier = JWT.require(Algorithm
                .HMAC512(environment.getProperty("TOKEN_SECRET"))).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        assertThat(decodedJWT.getAlgorithm()).isEqualTo("HS512");
        assertThat(decodedJWT.getSubject()).isEqualTo("johndoe@mymemo.app");
        assertThat(decodedJWT.getIssuer()).contains("/api/v*/registerTheUser");
        assertThat(decodedJWT.getClaim("token-type").asString())
                .isEqualTo("refresh token");
        assertThat(decodedJWT.getExpiresAt())
                .isAfterOrEqualTo(new Date(System.currentTimeMillis()))
                .isBefore(
                        (new Date(System.currentTimeMillis()))
                                .toInstant().plusMillis(24*60*60*1000));
    }
}