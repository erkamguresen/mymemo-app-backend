package app.mymemo.backend.security.filter;

import app.mymemo.backend.security.DecodedJWTAccessToken;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Provides Custom Authentication Filter.
 *
 * Author: Erkam Guresen
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final String TOKEN_SECRET;

    /**
     * This method will intercept every request that comes to the app.
     *
     * @param request the incoming request.
     * @param response the response that will be returned.
     * @param filterChain An object provided by the servlet container
     *                    to the developer giving a view into the
     *                    invocation chain of a filtered request
     *                    for a resource. This will be used
     *                    to invoke the next filter in the chain.
     * @throws ServletException – in case of errors.
     * @throws IOException - if an I/O error occurs during the processing
     * of the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/login")
                || request.getServletPath().equals("/api/v1/login/token/refresh" )){
            filterChain.doFilter(request,response);
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
//                String token = authorizationHeader.substring("Bearer ".length());

                try {
                    DecodedJWT decodedJWT = DecodedJWTAccessToken
                            .getVerifiedDecodedJWT(
                                    authorizationHeader,
                                    this.TOKEN_SECRET
                            );

                    String username = decodedJWT.getSubject();
                    // access the payload with key
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
                    Arrays.stream(roles).forEach(role->{
                        authorities.add(new SimpleGrantedAuthority(role));
                    });

                    // Tell spring the user and his/her roles or authorities
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    filterChain.doFilter(request,response);

                } catch (Exception e){
                    log.error("Error logging in with token: {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
//                    response.sendError(HttpStatus.FORBIDDEN.value());

                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", e.getMessage());

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);


                }
            } else {
                filterChain.doFilter(request,response);
            }
        }
    }
}
