package app.mymemo.backend.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("web.mail")
public class EmailConfiguration {

}
