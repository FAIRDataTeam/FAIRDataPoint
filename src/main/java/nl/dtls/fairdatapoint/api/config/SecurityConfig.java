package nl.dtls.fairdatapoint.api.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
//@Configuration
//@EnableWebSecurity
@PropertySource({"${fdp.server.conf:classpath:/conf/fdp-server.properties}"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {    
    
    @Value("${ipAddresses}")
    private String ipAddresses;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ipAddresses = ipAddresses.trim();
        List<String> address = Arrays.asList(ipAddresses.split(","));
        if(!address.isEmpty()) {
            String expression = "";
            for(String ip:address){                
                if(expression.contains("hasIpAddress(")) {
                    expression = expression + " or ";
                }
                expression = expression + "hasIpAddress('"+ip+"')";                
            }
            http.authorizeRequests().antMatchers(HttpMethod.POST)
                    .access(expression).anyRequest().permitAll().and().csrf()
                    .disable();
        }         
    } 
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer 
        propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
