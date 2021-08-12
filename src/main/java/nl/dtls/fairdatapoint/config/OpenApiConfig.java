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
package nl.dtls.fairdatapoint.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import nl.dtls.fairdatapoint.config.properties.InstanceProperties;
import nl.dtls.fairdatapoint.config.properties.OpenApiProperties;
import nl.dtls.fairdatapoint.service.openapi.OpenApiTagsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(InstanceProperties instanceProperties, OpenApiProperties openApiProperties) {
        OpenAPI openAPI = new OpenAPI()
                .servers(Collections.singletonList(new Server().url(instanceProperties.getUrl())))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )
                )
                .info(new Info()
                        .title(openApiProperties.getTitle())
                        .description(openApiProperties.getDescription())
                        .version(openApiProperties.getVersion())
                        .license(
                                new License()
                                        .name("The MIT License")
                                        .url("https://opensource.org/licenses/MIT")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt", Arrays.asList("read", "write"))
                )
                .tags(OpenApiTagsUtils.STATIC_TAGS);
        if (openApiProperties.getContact().getUrl() != null) {
            openAPI.getInfo().contact(new Contact()
                    .url(openApiProperties.getContact().getUrl())
                    .name(openApiProperties.getContact().getName())
                    .email(openApiProperties.getContact().getEmail())
            );
        }
        openAPI.servers(Collections.singletonList(new Server().url(instanceProperties.getUrl())));
        openAPI.setExtensions(new LinkedHashMap<>());
        openAPI.getExtensions().put("fdpGenericPaths", new Paths());
        openAPI.setPaths(new Paths());
        return openAPI;
    }
}
