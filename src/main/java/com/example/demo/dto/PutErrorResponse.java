package com.example.demo.dto;

/**
 * Error response schema used only for PUT endpoints.
 */
public record PutErrorResponse(int status, String message) {

    public static PutErrorResponse notFound(String message) {
        return new PutErrorResponse(404, message);
    }
}

