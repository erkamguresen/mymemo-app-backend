package app.mymemo.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class DecodedJWTAccessToken {

    private String algorithm;
    private String username;
    private String[] roles;
    private String issuer;
    private Date expiresAt;
    private String userId;

    public DecodedJWTAccessToken(HttpServletRequest request,String secretKey) {

        String requestHeader = request.getHeader("authorization");
        String authorizationToken = requestHeader.substring("Bearer ".length());

        DecodedJWT decodedJWT = getVerifiedDecodedJWT(authorizationToken, secretKey);

        this.algorithm = decodedJWT.getAlgorithm();
        this.username = decodedJWT.getSubject();
        this.roles = decodedJWT.getClaim("roles").asArray(String.class);
        this.issuer = decodedJWT.getIssuer();
        this.expiresAt = decodedJWT.getExpiresAt();
        this.userId = decodedJWT.getClaim("userId").asString();
    }

    public DecodedJWTAccessToken(
            String algorithm,
            String username,
            String[] roles,
            String issuer,
            Date expiresAt,
            String userId) {
        this.algorithm = algorithm;
        this.username = username;
        this.roles = roles;
        this.issuer = issuer;
        this.expiresAt = expiresAt;
        this.userId = userId;
    }

    public static DecodedJWT getVerifiedDecodedJWT(String authorizationTokenHeader, String secretKey){
        String[] authorizationTokenChunks = authorizationTokenHeader.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String jwtHeader = new String(decoder.decode(authorizationTokenChunks[0]));
        String jwtPayload = new String(decoder.decode(authorizationTokenChunks[1]));

        JSONObject jwtHeaderObject = new JSONObject(jwtHeader);

        Algorithm algorithm = findJWTAlgorithm(
                jwtHeaderObject.get("alg").toString(),
                secretKey
        );

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(authorizationTokenHeader);
    }

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
}
