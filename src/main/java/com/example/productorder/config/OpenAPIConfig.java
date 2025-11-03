package com.example.productorder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI productOrderOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8086");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setEmail("dev@productorder.com");
        contact.setName("Product Order API Team");
        contact.setUrl("https://github.com/yourusername/product-order-api");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Product Order Management API")
                .version("1.0.0")
                .contact(contact)
                .description("RESTful API for managing products and orders with Spring Boot")
                .termsOfService("https://www.example.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}

