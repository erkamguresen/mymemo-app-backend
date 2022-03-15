package app.mymemo.backend.security.filter;

import app.mymemo.backend.appuser.AppUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


//To log use
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter  {
    private final AuthenticationManager authenticationManager;
    private final String TOKEN_SECRET;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
//        return super.attemptAuthentication(request, response);
         String username = request.getParameter("username");
         String password = request.getParameter("password");
         log.info("Username is: {}", username);
         log.info("Password is: {}", password);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        AppUser user = (AppUser) authentication.getPrincipal();

        // Save the secret somewhere encrypted and then load and decrypt then pass here
//        Algorithm algorithm = Algorithm.HMAC256("TOKEN_SECRET");
        Algorithm algorithm = Algorithm.HMAC256(this.TOKEN_SECRET);

        String access_token = JWT.create()
                // use stg unique
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create()
                // use stg unique
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                // here I used a more complex algorithm
//                .sign(Algorithm.HMAC512(this.environment.getProperty("TOKEN_SECRET")));
                .sign(Algorithm.HMAC512("TOKEN_SECRET"));



/* //send in body instead of headers
        response.setHeader("access_token", access_token);
        response.setHeader("refresh_token", refresh_token);*/

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
