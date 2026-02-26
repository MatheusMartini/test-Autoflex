package com.myapp.exception;

public record ApiErrorResponse(
        int status,
        String message,
        String timestamp,
        String path
) {}
