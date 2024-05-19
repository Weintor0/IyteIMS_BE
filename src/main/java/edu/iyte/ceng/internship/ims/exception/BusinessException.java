package edu.iyte.ceng.internship.ims.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException {
    @Getter
    private final ErrorCode errorCode;
    private final String message;

    public BusinessException(ErrorCode errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
