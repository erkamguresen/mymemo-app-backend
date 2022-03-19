package app.mymemo.backend.security;

import app.mymemo.backend.appuser.AppUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides core JWT token decode methods and decoded tokens.
 *
 * Author: Erkam Guresen
 */
@Service
@RequiredArgsConstructor
public class JWTTokenService{

    private final Environment environment;

    /**
     * Decodes Authorization Header containing a JWT.
     * Uses a generic JWTVerifier to decode.
     *
     * @param authorizationTokenHeader Authorization Token Header containing "Bearer ".
     * @return a decodedJWT if verified otherwise error.
     */
    public DecodedJWT getVerifiedDecodedJWTFromHeader(String authorizationTokenHeader){

        String tokenInHeader = authorizationTokenHeader.substring("Bearer ".length());

        return getVerifiedDecodedJWTFromToken(tokenInHeader);
    }

    /**
     * Decodes a JWT using generic JWTVerifier.
     * Uses a generic JWTVerifier to decode.
     *
     * @param token JWT token to be decoded and verified
     *              (without header part "Bearer ").
     * @return a decodedJWT if verified otherwise error.
     */
    public DecodedJWT getVerifiedDecodedJWTFromToken(String token){

        String secretKey = this.environment.getProperty("TOKEN_SECRET");

        return getVerifiedDecodedJWTFromToken(token, secretKey);
    }

    /**
     * A static method to decode Authorization Header containing a JWT.
     * Uses a generic JWTVerifier to decode.
     *
     * @param authorizationTokenHeader Authorization Token Header containing "Bearer ".
     * @param secretKey secret key used to encode the JWT token itself.
     * @return a decodedJWT if verified otherwise error.
     */
    public static DecodedJWT getVerifiedDecodedJWTFromHeader(
            String authorizationTokenHeader, String secretKey){

        String tokenInHeader = authorizationTokenHeader.substring("Bearer ".length());

        return getVerifiedDecodedJWTFromToken(tokenInHeader, secretKey);
    }

    /**
     * A static method to decode a JWT using generic JWTVerifier.
     * Uses a generic JWTVerifier to decode.
     *
     * @param token JWT token to be decoded and verified
     *              (without header part "Bearer ").
     * @param secretKey secret key used to encode the JWT token itself.
     * @return a decodedJWT if verified otherwise error.
     */
    public static DecodedJWT getVerifiedDecodedJWTFromToken(
            String token, String secretKey){

        String[] authorizationTokenChunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String jwtHeader = new String(decoder.decode(authorizationTokenChunks[0]));

        JSONObject jwtHeaderObject = new JSONObject(jwtHeader);

        Algorithm algorithm = findJWTAlgorithm(
                jwtHeaderObject.get("alg").toString(),
                secretKey
        );

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(token);
    }

    /**
     * Finds the algorithm that is used to encode the token. Then uses
     * the secret to create te verification algorithm.
     *
     * @param alg name of the algorithm used in JWT token as presented in JWT token.
     * @param secretKey secret key used to encode the JWT token itself.
     * @return an algorithm, which is made from the algorithm type and the secret key,
     *          that can verify the JWT token.
     */
    private static Algorithm findJWTAlgorithm(String alg, String secretKey) {
        Algorithm algorithm;

        switch (alg) {
            case "HS256":
            case "HMAC256":
                algorithm = Algorithm.HMAC256(secretKey);
                break;
            case "HMAC512":
            case "HS512":
                algorithm = Algorithm.HMAC512(secretKey);
                break;
            case "HMAC384":
            case "HS384":
            default:
                algorithm =Algorithm.HMAC384(secretKey);
                break;
        }
        return  algorithm;
    }

    /**
     * Decodes the id of AppUser form a HttpRequest.
     *
     * @param request HttpServletRequest to be searched for AppUser id.
     * @return decoded id of the AppUSer from the JWT token.
     */
    public String getAppUserIdFromHttpRequest(HttpServletRequest request){
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return getAppUserIdFromAuthorizationHeader(tokenHeader);
    }

    /**
     * Decodes the id of AppUser form an Authorization Header.
     *
     * @param authorizationHeader Authorization Token Header containing "Bearer ".
     * @return decoded id of the AppUSer from the JWT token.
     */
    public String getAppUserIdFromAuthorizationHeader(String authorizationHeader){
        DecodedJWT decodedJWT = getVerifiedDecodedJWTFromHeader(authorizationHeader);

        return decodedJWT.getClaim("userId").asString();
    }

    /**
     * Creates an access token for the AppUser. It will be valid for 30 min.
     * It contains userId and user roles as extra claims.
     *
     * @param appUser the app user for whom the access wil be created.
     * @param issuer the url of the token requester.
     * @return an access token for the given app user.
     */
    public String createAccessToken(AppUser appUser, String issuer){
        return JWT.create()
                .withSubject(appUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .withIssuer(issuer)
                .withClaim("roles",
                        appUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .withClaim("userId",appUser.getId())
                .sign(Algorithm.HMAC256(this.environment.getProperty("TOKEN_SECRET")));
    }

    /**
     * Creates a refresh token for the AppUser. It will be valid for 1 day.
     * It will be used to update the access token.
     *
     * It contains "token-type" as an extra claim. This claim will be required
     * to refresh access tokens.
     *
     * @param appUser the app user for whom the access wil be created.
     * @param issuer the url of the token requester.
     * @return a refresh token for the given app user.
     */
    public String createRefreshToken(AppUser appUser, String issuer){
        return JWT.create()
                // use stg unique
                .withSubject(appUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .withIssuer(issuer)
                // add a claim to differentiate with access token
                .withClaim("token-type","refresh token")
                // here I used a more complex algorithm
                .sign(Algorithm.HMAC512(this.environment.getProperty("TOKEN_SECRET")));
    }
}
