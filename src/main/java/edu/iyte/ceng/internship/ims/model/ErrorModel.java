package edu.iyte.ceng.internship.ims.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class ErrorModel {
    private final LocalDateTime timestamp;
    private final List<Error> errors;

    public ErrorModel(List<Error> errors) {
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    @AllArgsConstructor
    @Getter
    @Builder
    @Data
    public static class Error {
        private String entity;
        private String attribute;
        private String constraint;
        private String message;
    }
}
