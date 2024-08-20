package com.ElasticSearch.Exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.management.openmbean.KeyAlreadyExistsException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errors = new HashMap<>();
        List<String> errorList = Arrays.asList(ex.getMessage().split(", "));
        errors.put("Validation Errors", errorList);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(KeyAlreadyExistsException.class)
    public ResponseEntity<?> handleIllegalDuplicateException(KeyAlreadyExistsException ex){
            Map<String, String> errors = new HashMap<>();
            errors.put("Error", ex.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

