package app.mymemo.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Provides core Configuration for user password encoding.
 *
 * Author: Erkam Guresen
 */
@Configuration
public class PasswordEncoder {

    /**
     * @return a new BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
