package com.example.progetto;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**") // Applica a tutti gli endpoint che iniziano con /api/
            .allowedOrigins("https://localhost") // Consenti richieste da questo indirizzo
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metodi consentiti
            .allowedHeaders("*") // Consenti tutti gli header
            .allowCredentials(true); // Consenti l'invio di cookie 
    }
}