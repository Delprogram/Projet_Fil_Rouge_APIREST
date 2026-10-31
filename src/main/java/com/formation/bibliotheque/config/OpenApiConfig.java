package com.formation.bibliotheque.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Équipe Formation");
        contact.setEmail("formation@exemple.fr");

        Info info = new Info();
        info.setTitle("API Bibliothèque");
        info.setVersion("1.0.0");
        info.setDescription("Projet fil rouge — gestion des auteurs, des livres et des emprunts");
        info.setContact(contact);

        OpenAPI openApi = new OpenAPI();
        openApi.setInfo(info);
        return openApi;
    }
}
