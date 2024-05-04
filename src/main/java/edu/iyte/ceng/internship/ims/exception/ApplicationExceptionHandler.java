package edu.iyte.ceng.internship.ims.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import java.lang.reflect.Field;
import java.util.*;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        ErrorResponse.Error error = ErrorResponse.Error.builder()
                .constraint(ex.getErrorCode().name())
                .message(ex.getMessage())
                .entity(null)
                .attribute(null)
                .build();

        ErrorResponse response = new ErrorResponse(List.of(error));
        return new ResponseEntity<>(response, ex.getErrorCode().getHttpStatusCode());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        List<ErrorResponse.Error> errors = new ArrayList<>();

        // Look for all classes annotated with Table (which are entity classes)
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Table.class));
        for (BeanDefinition bd : scanner.findCandidateComponents("edu.iyte.ceng.internship.ims.entity")) {
            Class<?> clazz;
            try {
                clazz = Class.forName(bd.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }

            // Get entity name
            AssociatedWithEntity awe = clazz.getAnnotation(AssociatedWithEntity.class);
            String entityName = awe.entityName();

            // Map each column name in the database to the corresponding attribute name in the entity class.
            Map<String, String> attributeToColumnMap = new HashMap<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column columnAnnotation = field.getAnnotation(Column.class);
                    attributeToColumnMap.put(columnAnnotation.name(), field.getName());
                }
            }

            // Iterate over all uniqueness constraints in the table.
            Table tbl = clazz.getAnnotation(Table.class);
            for (UniqueConstraint uniqueConstraint : tbl.uniqueConstraints()) {
                String constraintName = uniqueConstraint.name();
                // If the exception that was thrown contains the name of the uniqueness constraint in its
                // message, determine attribute name and add the error to the list.
                if (ex.getMessage().contains(constraintName)) {
                    String columnName = uniqueConstraint.columnNames()[0];
                    String attributeName = attributeToColumnMap.get(columnName);

                    ErrorResponse.Error error = ErrorResponse.Error.builder()
                            .entity(entityName)
                            .attribute(attributeName)
                            .constraint("Unique")
                            .message("")
                            .build();

                    errors.add(error);
                }
            }
        }

        ErrorResponse response = new ErrorResponse(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ErrorResponse.Error> errors = new ArrayList<>();

        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();

        String entity = violation.getLeafBean().getClass().getAnnotation(AssociatedWithEntity.class).entityName();
        String attribute = null;
        for (Path.Node node : violation.getPropertyPath()) {
            attribute = node.getName();
        }

        String constraint = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
        String message = violation.getMessage();

        ErrorResponse.Error error = ErrorResponse.Error.builder()
                .entity(entity)
                .attribute(attribute)
                .constraint(constraint)
                .message(message)
                .build();
        errors.add(error);

        ErrorResponse response = new ErrorResponse(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<ErrorResponse.Error> errors = new ArrayList<>();
        Throwable thr = ex.getCause();
        if (thr instanceof InvalidFormatException formatException) {
            List<JsonMappingException.Reference> path = formatException.getPath();
            for (JsonMappingException.Reference ref : path) {
                String field = ref.getFieldName();
                AssociatedWithEntity entityAnnotation = ref.getFrom().getClass().getAnnotation(AssociatedWithEntity.class);
                String entity = entityAnnotation.entityName();

                ErrorResponse.Error errorEntry = ErrorResponse.Error.builder()
                        .entity(entity)
                        .attribute(field)
                        .constraint("Format")
                        .message(thr.getMessage())
                        .build();

                errors.add(errorEntry);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ErrorResponse response = new ErrorResponse(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        List<ErrorResponse.Error> errors = new ArrayList<>();

        // Get entity name where the constraint was violated.
        Object target = ex.getTarget();
        String entity = null;
        if (target != null) {
            entity = target.getClass().getAnnotation(AssociatedWithEntity.class).entityName();
        }

        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (ObjectError err : allErrors) {
            if (err instanceof FieldError fieldError) {
                ErrorResponse.Error error = ErrorResponse.Error.builder()
                        .entity(entity)
                        .attribute(fieldError.getField())
                        .constraint(fieldError.getCode())
                        .message(fieldError.getDefaultMessage())
                        .build();
                errors.add(error);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        ErrorResponse response = new ErrorResponse(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
