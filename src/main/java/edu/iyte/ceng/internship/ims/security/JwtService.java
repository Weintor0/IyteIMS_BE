package edu.iyte.ceng.internship.ims.security;

import org.springframework.security.core.Authentication;

public interface JwtService {
    Authentication verifyToken(String token);

    String createToken(String subject);
}
