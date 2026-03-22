package com.example.demo.exception;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.demo.dto.ApiErrorResponse;
import com.example.demo.dto.PutErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        if (message == null || message.isBlank()) {
            message = "Invalid request payload";
        }

        if ("PUT".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.badRequest().body(new PutErrorResponse(400, message));
        }

        return ResponseEntity.badRequest().body(new ApiErrorResponse(400, "Bad Request", message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        if (message == null || message.isBlank()) {
            message = "Invalid request parameters";
        }

        if ("PUT".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.badRequest().body(new PutErrorResponse(400, message));
        }

        return ResponseEntity.badRequest().body(new ApiErrorResponse(400, "Bad Request", message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid value for field: " + ex.getName();
        if ("PUT".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.badRequest().body(new PutErrorResponse(400, message));
        }

        return ResponseEntity.badRequest().body(new ApiErrorResponse(400, "Bad Request", message));
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<?> handleMenuItemNotFound(MenuItemNotFoundException ex, HttpServletRequest request) {
        if ("PUT".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.status(404).body(PutErrorResponse.notFound(ex.getMessage()));
        }
        return ResponseEntity.status(404).body(new ApiErrorResponse(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(NoFieldsProvidedException.class)
    public ResponseEntity<?> handleNoFieldsProvided(NoFieldsProvidedException ex, HttpServletRequest request) {
        if ("PUT".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.badRequest().body(new PutErrorResponse(400, ex.getMessage()));
        }
        return ResponseEntity.badRequest().body(new ApiErrorResponse(400, "Bad Request", ex.getMessage()));
    }
}

