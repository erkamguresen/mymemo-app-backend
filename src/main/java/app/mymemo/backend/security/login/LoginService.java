package app.mymemo.backend.security.login;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserService;
import app.mymemo.backend.security.JWTTokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AppUserService userService;
    private final JWTTokenService jwtTokenService;
    private final Environment environment;

    /**
     * Refreshes access token by using a refresh token.
     *
     * @param request Http Request of the user.
     * @param response Http Response.
     * @throws IOException - if faces difficulties.
     */
    public void refreshAccessTokenByRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response
            ) throws IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            String refresh_token = authorizationHeader.substring("Bearer ".length());

            try {
                String secretKey = this.environment.getProperty("TOKEN_SECRET");

                DecodedJWT decodedJWT =
                        jwtTokenService.getVerifiedDecodedJWTFromHeader(authorizationHeader);

                String username = decodedJWT.getSubject();

                //find user in the DB
                AppUser appUser = (AppUser) userService.loadUserByUsername(username);

                String access_token = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
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

                response.setHeader("error", e.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());

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
