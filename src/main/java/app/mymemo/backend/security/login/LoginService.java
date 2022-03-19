package app.mymemo.backend.security.login;

import app.mymemo.backend.appuser.AppUser;
import app.mymemo.backend.appuser.AppUserService;
import app.mymemo.backend.exception.BadRequestException;
import app.mymemo.backend.security.JWTTokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

                DecodedJWT decodedJWT =
                        jwtTokenService.getVerifiedDecodedJWTFromHeader(authorizationHeader);

                if (!"refresh token".equals(decodedJWT.getClaim("token-type").asString()))
                    throw new RuntimeException("Refresh Token is missing");

                String username = decodedJWT.getSubject();

                //find user in the DB
                AppUser appUser = (AppUser) userService.loadUserByUsername(username);

                if (appUser == null)
                    throw new BadRequestException("User does not exist");

                String access_token = jwtTokenService.createAccessToken(
                        appUser,
                        request.getRequestURL().toString() );

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
