package edu.iyte.ceng.internship.ims.exception;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().name(), ex.getMessage(), Arrays.asList());
        return new ResponseEntity<>(response, ex.getErrorCode().getHttpStatusCode());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        List<AttributeError> errors = new ArrayList<>();

        final String[] integrityErrors = new String[] {
                "UC_EMAIL", User.entityName, "email", "Unique", "",
                "UC_STUDENT_NUMBER", Student.entityName, "studentNumber", "Unique", "",
                "UC_FIRM_NAME", Firm.entityName, "firmName", "Unique", "",
                "UC_BUSINESS_REGISTRATION_NUMBER", Firm.entityName, "businessRegistrationNumber", "Unique", ""
        };

        String message = ex.getMessage();
        for (int i = 0; i < integrityErrors.length / 5; i++) {
            String constraintName = integrityErrors[i * 5];
            if (message.contains(constraintName)) {
                AttributeError error = AttributeError.builder()
                        .entity(integrityErrors[i*5 + 1])
                        .attribute(integrityErrors[i*5 + 2])
                        .constraint(integrityErrors[i*5 + 3])
                        .message(integrityErrors[i*5 + 4])
                        .build();

                errors.add(error);
            }
        }

        ErrorResponse response = new ErrorResponse("ConstraintViolation", "", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<AttributeError> errors = new ArrayList<>();

        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();

        String entity = violation.getLeafBean().getClass().getAnnotation(AssociatedWithEntity.class).entityName();
        String attribute = null;
        for (Path.Node node : violation.getPropertyPath()) {
            attribute = node.getName();
        }

        String constraint = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
        String message = violation.getMessage();

        AttributeError error = AttributeError.builder()
                .entity(entity)
                .attribute(attribute)
                .constraint(constraint)
                .message(message)
                .build();
        errors.add(error);

        ErrorResponse response = new ErrorResponse("ConstraintViolation", "", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        List<AttributeError> errors = new ArrayList<>();

        // Get entity name where the constraint was violated.
        Object target = ex.getTarget();
        String entity = null;
        if (target != null) {
            entity = target.getClass().getAnnotation(AssociatedWithEntity.class).entityName();
        }

        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (ObjectError err : allErrors) {
            if (err instanceof FieldError) {
                FieldError fieldError = (FieldError)err;
                AttributeError error = AttributeError.builder()
                        .entity(entity)
                        .attribute(fieldError.getField())
                        .constraint(fieldError.getCode())
                        .message(fieldError.getDefaultMessage())
                        .build();
                errors.add(error);
            }
        }

        ErrorResponse response = new ErrorResponse("ConstraintViolation", "", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
