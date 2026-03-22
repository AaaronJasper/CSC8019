package com.example.demo.dto;

/**
 * Standard JSON body for API errors (e.g. 404).
 */
public record ApiErrorResponse(int status, String error, String message) {

    public static ApiErrorResponse notFound(String message) {
        return new ApiErrorResponse(404, "Not Found", message);
    }
}
