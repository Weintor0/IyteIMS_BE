package edu.iyte.ceng.internship.ims.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowedMethods("GET", "PUT", "POST", "DELETE", "PATCH")
                .allowedOrigins("*")
                .allowCredentials(false);
    }
}
