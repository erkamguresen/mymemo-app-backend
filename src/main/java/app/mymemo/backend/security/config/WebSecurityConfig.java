package app.mymemo.backend.security.config;

import app.mymemo.backend.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);

//        http
//                .csrf().disable()
//                .authorizeRequests().antMatchers("/api/v*/registration/**")
//                .permitAll()
//                .anyRequest().authenticated().and().formLogin();

//        http
//                .csrf().disable()
//                .authorizeRequests().antMatchers("/api/v*/registration/**","/api/v*/users/**")
//                .permitAll()
//                .anyRequest().authenticated().and().formLogin();

        // To change login place from CustomAuthenticationFilter which extends
        // UsernamePasswordAuthenticationFilter which extends AbstractAuthenticationProcessingFilter

// TODO
//        CustomAuthenticationFilter customAuthenticationFilter =
//                new CustomAuthenticationFilter(authenticationManagerBean());
//        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");


        /**
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
                "/api/v*/users/**",
                "/api/v*/registration/**",
                "/api/v*/login/**", "/api/v*/token/refresh/**").permitAll();

        http.authorizeRequests().antMatchers(GET, "/api/v1/users/**").hasAnyAuthority("USER");
        http.authorizeRequests().antMatchers(POST, "/api/users/save/**").hasAnyAuthority("ADMIN");

        // Allow everyone to access this app
//        http.authorizeRequests().anyRequest().permitAll();

        http.authorizeRequests().anyRequest().authenticated();

        //Auth filter to check login
//        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));
// TODO
//        http.addFilter(customAuthenticationFilter);

        // TODO use before to intercept every request
//        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
//        // here we pass de password encoder to userdetials service
//        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);

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
