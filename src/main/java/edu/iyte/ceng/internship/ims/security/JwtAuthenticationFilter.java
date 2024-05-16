package edu.iyte.ceng.internship.ims.security;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.iyte.ceng.internship.ims.model.ErrorModel;
import edu.iyte.ceng.internship.ims.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.util.List;

import static edu.iyte.ceng.internship.ims.model.ErrorModel.Error;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);

        if (StringUtils.isNotEmpty(token)) {
            Authentication authentication = jwtService.verifyToken(token);

            // Check if the verification of the given token has failed.
            if (authentication == null) {
                returnInvalidTokenResponse(response);
                return;
            }

            // Check if the user who received this token deleted his/her account, even though the token can still be verified.
            if (userRepository.findById(authentication.getName()).isEmpty()) {
                returnInvalidTokenResponse(response);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private void returnInvalidTokenResponse(HttpServletResponse response) throws IOException {
        ErrorModel errorModel = new ErrorModel(
                List.of(Error.builder()
                        .entity(null)
                        .attribute(null)
                        .constraint("InvalidToken")
                        .message("Invalid Json Web Token Provided")
                        .build()));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String errorResponseJson = objectWriter.writeValueAsString(errorModel);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setHeader("Content-Type", "application/json");
        response.getWriter().print(errorResponseJson);
        response.getWriter().flush();
    }
}
