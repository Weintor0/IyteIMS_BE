package edu.iyte.ceng.internship.ims.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.iyte.ceng.internship.ims.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Date;

@Service
@Primary
public class JwtServiceAuth0 implements JwtService {
    private final SecurityConfig securityConfig;
    private static final Logger logger = LoggerFactory.getLogger(JwtServiceAuth0.class);

    @Autowired
    public JwtServiceAuth0(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    public Authentication verifyToken(String token) {
        try {
            String replacedToken = token.replace(SecurityConstants.TOKEN_PREFIX, "");
            DecodedJWT decodedToken = JWT.require(Algorithm.HMAC512(securityConfig.getJwtSecret()))
                    .build()
                    .verify(replacedToken);

            String subject = decodedToken.getSubject();
            if (StringUtils.isNotEmpty(subject)) {
                return new UsernamePasswordAuthenticationToken(subject, null, List.of());
            }
        } catch (JWTVerificationException exception) {
            logger.warn("JWT verification failed : {} : {}", token, exception.getMessage());
        }

        return null;
    }

    public String createToken(String subject) {
        String token = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(securityConfig.getJwtSecret()));

        return SecurityConstants.TOKEN_PREFIX + token;
    }
}
