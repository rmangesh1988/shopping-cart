package com.hardware.store.exception;

import com.hardware.store.domain.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiException> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND)
                .body(ApiException.builder()
                        .httpStatus(NOT_FOUND)
                        .message(e.getLocalizedMessage())
                        .errors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiException> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> violations = ex.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.toList());
        log.error("Validation/Constraint errors : "+violations);
        return ResponseEntity.status(BAD_REQUEST)
                .body(ApiException.builder()
                        .httpStatus(BAD_REQUEST)
                        .message("Validation/Constraint errors")
                        .errors(violations).build());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorMessages = buildErrorList(ex.getBindingResult());
        log.error("Validation errors : "+errorMessages);
        return ResponseEntity.status(status)
                .body(ApiException.builder()
                        .httpStatus(status)
                        .message("Request validation error")
                        .errors(errorMessages).build());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = ex.getParameterName() + " parameter is missing!";
        log.error("Parameter error : "+errorMessage);
        return ResponseEntity.status(status)
                .body(ApiException.builder()
                        .httpStatus(status)
                        .message(ex.getLocalizedMessage())
                        .errors(List.of(errorMessage)).build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiException> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage =
                ex.getName() + " should be of type " + ex.getRequiredType().getName();
        return ResponseEntity.status(BAD_REQUEST)
                .body(ApiException.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(ex.getLocalizedMessage())
                        .errors(List.of(errorMessage)).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiException> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ApiException.builder()
                        .httpStatus(UNAUTHORIZED)
                        .message(ex.getLocalizedMessage())
                        .errors(Collections.emptyList()).build());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleGenericException(Exception ex) throws Exception {
        if (ex instanceof ServletException) {
            throw ex;
        }
        log.error("Internal server error: " + ex.getMessage(), ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ApiException.builder()
                        .httpStatus(INTERNAL_SERVER_ERROR)
                        .message(ex.getLocalizedMessage())
                        .errors(Collections.EMPTY_LIST).build());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        super.handleExceptionInternal(ex, body, headers, status, request);
        return new ResponseEntity(ApiException.builder()
                .httpStatus(status)
                .message(ex.getLocalizedMessage())
                .errors(Collections.EMPTY_LIST).build()
                , headers, status);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiException> handleValidationException(ValidationException ex) {
        ApiException apiException = ApiException.builder()
                .httpStatus(BAD_REQUEST)
                .message(ex.getMessage())
                .errors(Collections.emptyList())
                .build();

        return ResponseEntity.badRequest().body(apiException);
    }

    private List<String> buildErrorList(BindingResult bindingResult) {
        List<String> fieldErrors = new ArrayList<>();
        List<String> globalErrors = new ArrayList<>();
        if(bindingResult.hasFieldErrors()) {
            fieldErrors = bindingResult.getFieldErrors().stream()
                    .map(fError -> fError.getField() + " " + fError.getDefaultMessage())
                    .collect(Collectors.toList());
        }
        if(bindingResult.hasGlobalErrors()) {
            globalErrors = bindingResult.getGlobalErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
        }
        return ListUtils.union(fieldErrors, globalErrors);
    }

}
