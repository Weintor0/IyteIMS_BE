package edu.iyte.ceng.internship.ims.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
    private final LocalDateTime timestamp;

    private final String errorCode;
    private final String errorMessage;

    private final List<AttributeError> attributeErrors;

    public ErrorResponse(String errorCode, String errorMessage, List<AttributeError> attributeErrors) {
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.attributeErrors = attributeErrors;
    }
}
