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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static nl.dtls.fairdatapoint.util.HttpUtil.removeProtocol;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${instance.clientUrl}")
    private String clientUrl;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .host(removeProtocol(clientUrl))
                .securitySchemes(List.of(apiKey()))
                .securityContexts(List.of(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "FAIR Data Point API",
                "<a target='_blank' " +
                        "    href='https://github.com/FAIRDataTeam/FAIRDataPoint/wiki/FAIR-Data-Point-Specification'>" +
                        "       FAIR Data Point Specification" +
                        "   </a>" +
                        "   <br/>",
                "1.8.0",
                "ATO",
                new Contact("Luiz Bonino",
                        "https://github.com/FAIRDataTeam/FAIRDataPoint",
                        "luiz.bonino@go-fair.org"),
                "The MIT License",
                "https://opensource.org/licenses/MIT",
                Collections.emptyList()
        );
    }

    private ApiKey apiKey() {
        return new ApiKey("apiKey", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = {new AuthorizationScope("global", "global description")};
        return List.of(new SecurityReference("apiKey", authorizationScopes));
    }

}
