/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.config;

import org.springframework.context.annotation.Bean;
import static springfox.documentation.builders.PathSelectors.regex;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configuration.
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-19
 * @version 0.1
 */
@EnableSwagger2
public class ApplicationSwaggerConfig {

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(regex("/.*"))
            .build()
            .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
            "FDP API Java based",
            "<p>This API is a prototype version, If you find bugs in this api "
                    + "please contact the developer.</p>"
                    + "<p>"
                    + "<li><a target='_blank' href = 'https://dtl-fair."
                    + "atlassian.net/wiki/display/FDP/FAIR+Data+Point+"
                    + "Software+Specification'>API specs</li>"
                    + "<li><a target='_blank' href = 'https://github.com/"
                    + "DTL-FAIRData/ODEX-FAIRDataPoint/tree/master/fdp-api"
                    + "/java'>Source code</a> </li></p>",
            "0.1 BETA",
            "ATO",
            "r.kaliyaperumal@lumc.nl",
            "CC BY-NC-ND 3.0",
            "http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"
        );
        return apiInfo;
    }
}
