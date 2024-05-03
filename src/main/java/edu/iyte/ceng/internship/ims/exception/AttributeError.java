package edu.iyte.ceng.internship.ims.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@Data
public class AttributeError {
    private String entity;
    private String attribute;
    private String constraint;
    private String message;
}