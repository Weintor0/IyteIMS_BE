package edu.iyte.ceng.internship.ims.exception;

public class BusinessException extends RuntimeException {
    private final String message;
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
