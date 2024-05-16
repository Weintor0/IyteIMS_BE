package edu.iyte.ceng.internship.ims.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/auth/**")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowedMethods("POST")
                .allowedOrigins("*")
                .allowCredentials(false);
    }
}
