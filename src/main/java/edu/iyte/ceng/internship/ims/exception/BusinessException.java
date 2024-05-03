package edu.iyte.ceng.internship.ims.exception;

public class BusinessException extends RuntimeException {
    private final BusinessExceptionType errorCode;
    private final String message;

    public BusinessException(BusinessExceptionType errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BusinessExceptionType getErrorCode() {
        return errorCode;
    }
}
