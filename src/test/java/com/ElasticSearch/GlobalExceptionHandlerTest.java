package com.ElasticSearch;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ElasticSearch.Exception.GlobalExceptionHandler;

import javax.management.openmbean.KeyAlreadyExistsException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
public void testHandleIllegalArgumentException() {
    IllegalArgumentException ex = new IllegalArgumentException("Invalid input provided");
    ResponseEntity<?> response = globalExceptionHandler.handleIllegalArgumentException(ex);

    // Check the status code
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    // Safely cast and extract the response body
    Object responseBody = response.getBody();
    if (responseBody instanceof Map) {
        Map<?, ?> responseBodyMap = (Map<?, ?>) responseBody;
        Object validationErrors = responseBodyMap.get("Validation Errors");
        
        if (validationErrors instanceof List) {
            List<?> validationErrorsList = (List<?>) validationErrors;
            assertEquals("Invalid input provided", validationErrorsList.get(0));
        }
    }
}

    

    @Test
    public void testHandleKeyAlreadyExistsException() {
        KeyAlreadyExistsException ex = new KeyAlreadyExistsException("Key already exists");
        ResponseEntity<?> response = globalExceptionHandler.handleIllegalDuplicateException(ex);

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Key already exists", responseBody.get("Error"));
    }

    @Test
    public void testHandleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("No such element found");
        ResponseEntity<?> response = globalExceptionHandler.handleNoSuchElementException(ex);

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No such element found", responseBody.get("Error"));
    }

    @Test
    public void testHandleIllegalStateException() {
        IllegalStateException ex = new IllegalStateException("Illegal state occurred");
        ResponseEntity<?> response = globalExceptionHandler.handleIllegalStateException(ex);

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Illegal state occurred", responseBody.get("Error"));
    }

    @Test
    public void testHandleException() {
        Exception ex = new Exception("Internal server error");
        ResponseEntity<?> response = globalExceptionHandler.handleExceptions(ex);

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", responseBody.get("Error"));
    }
}
