package kr.ac.korea.gdg.disasterassistantforblind.modules.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Disaster Assistant for Blind API")
                        .description("API documentation for the Disaster Assistant for Blind application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Korea University GDG")
                                .url("https://gdg.korea.ac.kr")
                                .email("gdg@korea.ac.kr")))
                .addServersItem(new Server()
                        .url("/")
                        .description("Default Server URL"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Basic authentication")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}