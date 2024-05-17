package edu.iyte.ceng.internship.ims.security;

import edu.iyte.ceng.internship.ims.entity.UserRole;
import org.springframework.security.core.Authentication;

public interface JwtService {
    Authentication verifyToken(String token);

    String createToken(String subject, UserRole role);
}
