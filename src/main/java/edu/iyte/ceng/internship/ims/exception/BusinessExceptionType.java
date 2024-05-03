package edu.iyte.ceng.internship.ims.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessExceptionType {
    Unknown(HttpStatus.BAD_REQUEST),
    Unauthorized(HttpStatus.UNAUTHORIZED),
    Forbidden(HttpStatus.FORBIDDEN),
    ResourceMissing(HttpStatus.NOT_FOUND),
    AccountAlreadyExists(HttpStatus.CONFLICT),
    AccountMissing(HttpStatus.NOT_FOUND),
    PasswordMismatch(HttpStatus.FORBIDDEN);

    private final HttpStatus httpStatusCode;
}
