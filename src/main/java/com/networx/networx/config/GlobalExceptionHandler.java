package com.networx.networx.config;

import com.networx.networx.dto.responses.ApiResponse;
import com.networx.networx.exceptions.BadArgumentException;
import com.networx.networx.exceptions.InvalidTokenException;
import com.networx.networx.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private GlobalResponseHandler responseHandler;
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return responseHandler.wrapResponse(null, "Invalid input for parameter: '" + ex.getName() + "'. Expected type: " + ex.getRequiredType().getSimpleName(), false, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return responseHandler.wrapResponse(null, ex.getMessage(), false, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus
    @ExceptionHandler(BadArgumentException.class)
    public ResponseEntity<?> handleBadArgumentException(BadArgumentException ex) {
        return responseHandler.wrapResponse(null, ex.getMessage(), false, HttpStatus.BAD_REQUEST);
    }
    // Handle JWT Expired Token Exception
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex) {
        return responseHandler.wrapResponse(null, "Token expired", false, HttpStatus.UNAUTHORIZED);
    }

    // Handle Invalid Token Exception
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex) {
        return responseHandler.wrapResponse(null, "Invalid token", false, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception e) {
        return responseHandler.wrapResponse(null, e.getMessage(), false, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}