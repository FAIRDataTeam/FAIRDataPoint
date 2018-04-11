/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.dtls.fairdatapoint.api.config;

//import java.util.Arrays;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
/**
 * SecurityConfig to limit POST calls to localhost
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-09-20
 * @version 0.1
 */
//@Configuration
//@EnableWebSecurity
//@PropertySource({"${fdp.server.conf:classpath:/conf/fdp-server.properties}"})
//public class SecurityConfig extends WebSecurityConfigurerAdapter {    
//    
//    @Value("${ipAddresses}")
//    private String ipAddresses;
//    
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        ipAddresses = ipAddresses.trim();
//        List<String> address = Arrays.asList(ipAddresses.split(","));
//        if(!address.isEmpty()) {
//            String expression = "";
//            for(String ip:address){                
//                if(expression.contains("hasIpAddress(")) {
//                    expression = expression + " or ";
//                }
//                expression = expression + "hasIpAddress('"+ip+"')";                
//            }
//            http.authorizeRequests().antMatchers(HttpMethod.POST)
//                    .access(expression).anyRequest().permitAll().and().csrf()
//                    .disable();
//        }         
//    } 
//    
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer 
//        propertySourcesPlaceholderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }
//}
