package app.mymemo.backend.security.config;

import app.mymemo.backend.appuser.AppUserService;
import app.mymemo.backend.security.JWTTokenService;
import app.mymemo.backend.security.filter.CustomAuthenticationFilter;
import app.mymemo.backend.security.filter.CustomAuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Provides core Web Security Configuration.
 *
 * Author: Erkam Guresen
 */
@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // To change login place from CustomAuthenticationFilter which extends
        // UsernamePasswordAuthenticationFilter which extends AbstractAuthenticationProcessingFilter
        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(
                        authenticationManagerBean(),
                        new JWTTokenService(environment));

        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");


        /*
         * for JWT make the app stateless- normal spring app is session based
         * and uses cookies etc. to tract the user
         *
         * Use the following to make stateless RestFul api
         */

        // The order of this matters
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);

        //permit the following
        http.authorizeRequests().antMatchers(
                //TODO
//                "/api/v*/users/**",
                "/api/v*/registration/**",
                "/api/v*/login/**",
                "/api/v*/token/refresh/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/api/v1/users/**")
                .hasAnyAuthority("APP_USER_ROLE");

        /*
         * guest users can only get the deck but cannot create or change
         * unless their email, which is used as Id for guests, has the privileges.
         */
        http.authorizeRequests()
                .antMatchers( GET, "/api/v1/flash-card-deck/**")
                .hasAnyAuthority("APP_GUEST_USER_ROLE", "APP_USER_ROLE");

        http.authorizeRequests()
                .antMatchers( POST, "/api/v1/flash-card-deck/**")
                .hasAnyAuthority( "APP_GUEST_USER_ROLE","APP_USER_ROLE");

        // progress of the guest users cannot be saved.
        http.authorizeRequests()
                .antMatchers(  "/api/v1/user-progress/**")
                .hasAnyAuthority( "APP_USER_ROLE");

        // TODO for role based authorization prepare appAdmin service & routes
        http.authorizeRequests()
                .antMatchers( "/api/v1/admin/**")
                .hasAnyAuthority("APP_ADMIN_ROLE");

        // TODO for role based authorization prepare appSuperAdmin service & routes
        http.authorizeRequests()
                .antMatchers("/api/v1/super-admin/**")
                .hasAnyAuthority("APP_SUPER_ADMIN_ROLE");

        // Allow everyone to access this app
//        http.authorizeRequests().anyRequest().permitAll();

        http.authorizeRequests().anyRequest().authenticated();

        //Auth filter to check login
        http.addFilter(customAuthenticationFilter);

        // TODO use before to intercept every request
        http.addFilterBefore(new CustomAuthorizationFilter(
                this.environment.getProperty("TOKEN_SECRET")
                ),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        // here we pass de password encoder and userdetials service
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);

        return  provider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()
        throws Exception{
        return super.authenticationManagerBean();
    }
}
