package app.mymemo.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class DecodedJWTAccessTokenTest {


    private final String TOKEN_SECRET = "TOKEN_SECRET";
    private String token1, token2;

    @BeforeEach
    void setUp() {

        token1 = JWT.create()
                // use stg unique
                .withSubject("jhondoe@mymemo.app")
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
        assertThat(decodedJWT1.getSubject()).isEqualTo("jhondoe@mymemo.app");
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
        assertThat(decodedJWT1.getSubject()).isEqualTo("jhondoe@mymemo.app");
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
}