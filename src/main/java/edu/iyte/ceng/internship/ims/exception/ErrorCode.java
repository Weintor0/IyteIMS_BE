package edu.iyte.ceng.internship.ims.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    Unknown(400),
    Validation(422),
    Unauthorized(401),
    Forbidden(403),
    ResourceMissing(404),
    AccountAlreadyExists(409),
    AccountMissing(404),
    PasswordMismatch(409);

    private int errorCode;
}
