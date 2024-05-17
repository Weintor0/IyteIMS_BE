package edu.iyte.ceng.internship.ims.security;

import edu.iyte.ceng.internship.ims.entity.UserRole;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.iyte.ceng.internship.ims.config.SecurityConfig;

@Service
@Primary
public class JwtServiceJsonWebToken implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceJsonWebToken.class);

    private final SecurityConfig securityConfig;

    @Autowired
    public JwtServiceJsonWebToken(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    public Authentication verifyToken(String token) {
        if (StringUtils.isNotEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            try {
                byte[] signingKey = securityConfig.getJwtSecret().getBytes();

                Jws<Claims> parsedToken = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token.replace("Bearer ", ""));

                String subject = parsedToken
                        .getBody()
                        .getSubject();
                String role = parsedToken
                        .getBody()
                        .get(SecurityConstants.CLAIM_ROLE, String.class);

                if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(role)) {
                    return new UsernamePasswordAuthenticationToken(subject, null, List.of(new SimpleGrantedAuthority(role)));
                }
            } catch (ExpiredJwtException exception) {
                logger.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                logger.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                logger.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch (SignatureException exception) {
                logger.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                logger.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            }
        }
        return null;
    }

    public String createToken(String subject, UserRole userRole) {
        byte[] signingKey = securityConfig.getJwtSecret().getBytes();
        return SecurityConstants.TOKEN_PREFIX + Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                .setIssuer(SecurityConstants.TOKEN_ISSUER)
                .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                .setSubject(subject)
                .addClaims(Map.of(SecurityConstants.CLAIM_ROLE, userRole.getRoleName()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
                .compact();
    }
}
