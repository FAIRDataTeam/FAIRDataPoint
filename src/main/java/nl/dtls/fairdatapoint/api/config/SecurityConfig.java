package nl.dtls.fairdatapoint.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SecurityConfig to limit POST calls to localhost
 * 
 * @author Kees Burger
 * @since 2016-09-20
 * @version 0.1
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
         http.authorizeRequests()
                .antMatchers(HttpMethod.POST).hasIpAddress("127.0.0.1")
                .anyRequest().permitAll()
            .and()
            .csrf().disable();
    }
}
