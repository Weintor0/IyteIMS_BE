package edu.iyte.ceng.internship.ims.security;

public class SecurityConstants {
    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "ims";
    public static final String TOKEN_AUDIENCE = "ims";
    public static final String CLAIM_ROLE = "Role";
    public static final int TOKEN_EXPIRATION = 7200000; // 7200000 milliseconds = 7200 seconds = 2 hours.
}
