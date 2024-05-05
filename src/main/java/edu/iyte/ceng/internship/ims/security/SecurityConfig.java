package edu.iyte.ceng.internship.ims.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

import edu.iyte.ceng.internship.ims.repository.UserRepository;
import edu.iyte.ceng.internship.ims.security.filter.AuthenticationFilter;
import edu.iyte.ceng.internship.ims.security.filter.ExceptionHandlerFilter;
import edu.iyte.ceng.internship.ims.security.filter.AuthorizationFilter;
import edu.iyte.ceng.internship.ims.security.manager.CustomAuthenticationManager;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationManager customAuthenticationManager;
    private final UserRepository userRepository;

    @SuppressWarnings({ "deprecation", "removal" })
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(
            customAuthenticationManager, userRepository);
        authenticationFilter.setFilterProcessesUrl("/login");
        
        http
                .headers(headers -> headers.frameOptions().disable())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(requests -> requests
                        .requestMatchers("/h2/**").permitAll()
                        .requestMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH + "/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .addFilterAfter(new AuthorizationFilter(), AuthenticationFilter.class)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}