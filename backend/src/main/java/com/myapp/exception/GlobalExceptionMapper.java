package com.myapp.exception;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.stream.Collectors;

@Provider
@Priority(Priorities.USER)
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        int status = resolveStatus(exception);
        String message = resolveMessage(exception, status);
        String path = resolvePath();

        if (status >= 500) {
            LOG.errorf(exception, "Unhandled exception at path %s", path);
        } else {
            LOG.debugf("Handled exception at path %s with status %d: %s", path, status, message);
        }

        ApiErrorResponse payload = new ApiErrorResponse(
                status,
                message,
                Instant.now().toString(),
                path
        );

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(payload)
                .build();
    }

    private int resolveStatus(Throwable exception) {
        if (exception instanceof WebApplicationException webEx && webEx.getResponse() != null) {
            return webEx.getResponse().getStatus();
        }
        if (exception instanceof ConstraintViolationException) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        if (exception instanceof IllegalArgumentException) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    private String resolveMessage(Throwable exception, int status) {
        if (exception instanceof ConstraintViolationException violationException) {
            return formatConstraintViolations(violationException);
        }

        if (status >= 500) {
            return INTERNAL_SERVER_ERROR_MESSAGE;
        }

        if (exception instanceof WebApplicationException webEx) {
            String webMessage = webEx.getMessage();
            if (webMessage != null && !webMessage.isBlank()) {
                return webMessage;
            }
            if (webEx.getResponse() != null && webEx.getResponse().getStatusInfo() != null) {
                return webEx.getResponse().getStatusInfo().getReasonPhrase();
            }
        }

        String defaultMessage = exception.getMessage();
        if (defaultMessage != null && !defaultMessage.isBlank()) {
            return defaultMessage;
        }

        return Response.Status.fromStatusCode(status) != null
                ? Response.Status.fromStatusCode(status).getReasonPhrase()
                : "Unexpected error";
    }

    private String formatConstraintViolations(ConstraintViolationException exception) {
        if (exception.getConstraintViolations() == null || exception.getConstraintViolations().isEmpty()) {
            return "Validation failed";
        }

        return exception.getConstraintViolations().stream()
                .map(this::formatViolation)
                .collect(Collectors.joining("; "));
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath() != null
                ? violation.getPropertyPath().toString()
                : "field";
        String message = violation.getMessage() != null
                ? violation.getMessage()
                : "invalid value";
        return path + ": " + message;
    }

    private String resolvePath() {
        if (uriInfo == null || uriInfo.getRequestUri() == null) {
            return "";
        }
        return uriInfo.getRequestUri().getPath();
    }
}
