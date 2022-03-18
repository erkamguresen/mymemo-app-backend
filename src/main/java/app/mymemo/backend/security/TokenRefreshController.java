package app.mymemo.backend.security;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/login")
@AllArgsConstructor
public class TokenRefreshController {
    //TODO

    private final AppUserService userService;
    private final Environment environment;

    // You can inject HttpServletRequest request, HttpServletResponse response
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO repeating code with authorization
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            String refresh_token = authorizationHeader.substring("Bearer ".length());

            try {
                // TODO Refactor this part it is repeating and too long
                // https://www.baeldung.com/java-jwt-token-decode to get algorithm name
//                String[] tokenChunks = refresh_token.split("\\.");
//                Base64.Decoder decoder = Base64.getUrlDecoder();
//
//                String header = new String(decoder.decode(tokenChunks[0]));
//                String payload = new String(decoder.decode(tokenChunks[1]));
//
//                JSONObject jwtHeader = new JSONObject(header);
//
//
//
//                String secretKey = "secret";
//                Algorithm algorithm ;
//                String s =jwtHeader.get("alg").toString();
//
//                // in the token it is not HMAC but HS?
//                //TODO check typ and put inside try?
//                switch (jwtHeader.get("alg").toString()) {
//                    case "HS256":
//                    case "HMAC256":
//                        algorithm = Algorithm.HMAC256(secretKey);
//                        break;
//                    case "HMAC512":
//                    case "HS512":
//                        algorithm = Algorithm.HMAC512(secretKey);
//                        break;
//                    case "HMAC384":
//                    case "HS384":
//                    default:
//                        algorithm =Algorithm.HMAC384(secretKey);
//                        break;
//                }
//
//
//                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
//                DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);

                String secretKey = this.environment.getProperty("TOKEN_SECRET");

                DecodedJWT decodedJWT = DecodedJWTAccessToken
                        .getVerifiedDecodedJWT(
                                authorizationHeader,
                                secretKey
                        );;
                String username = decodedJWT.getSubject();

                //find user in the DB
                AppUser appUser = (AppUser) userService.loadUserByUsername(username);

                String access_token = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        // .withClaim("roles", appUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .withClaim("roles",
                                appUser.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toList()))
                        .sign(Algorithm.HMAC256(secretKey));

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);


            } catch (Exception e){
//                log.error("Error logging in with token: {}", e.getMessage());
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
//                    response.sendError(HttpStatus.FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);

            }
        } else {
            throw new RuntimeException("Refresh Token is missing");
        }
    }
}
