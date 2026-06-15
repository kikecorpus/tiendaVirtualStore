package com.tienda.virtualstore.exception;

import com.tienda.virtualstore.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Recurso no encontrado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(
                        HttpStatus.NOT_FOUND,
                        "NOT_FOUND",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // ── 409 Conflict ─────────────────────────────────────────
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        log.warn("Recurso duplicado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(
                        HttpStatus.CONFLICT,
                        "CONFLICT",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // ── 400 Bad Request — lógica de negocio ──────────────────
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Error de negocio: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        "BAD_REQUEST",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // ── 401 Unauthorized ─────────────────────────────────────
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {

        log.warn("No autorizado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(
                        HttpStatus.UNAUTHORIZED,
                        "UNAUTHORIZED",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // ── 401 — Spring Security Authentication ─────────────────
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(
                        HttpStatus.UNAUTHORIZED,
                        "UNAUTHORIZED",
                        "Credenciales inválidas",
                        request.getRequestURI()
                ));
    }

    // ── 403 — Spring Security Access Denied ──────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(
                        HttpStatus.FORBIDDEN,
                        "FORBIDDEN",
                        "No tienes permisos para realizar esta acción",
                        request.getRequestURI()
                ));
    }

    // ── 400 — Validaciones de @Valid ─────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        log.warn("Error de validación: {} campos inválidos", errors.size());

        ErrorResponse response = buildError(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Hay " + errors.size() + " campo(s) con error",
                request.getRequestURI()
        );
        response.setValidationErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ── 500 — Error genérico ──────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Error inesperado en {}: {}", request.getRequestURI(),
                ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "INTERNAL_SERVER_ERROR",
                        "Ocurrió un error inesperado. Intenta más tarde.",
                        request.getRequestURI()
                ));
    }

    // ── Helpers ───────────────────────────────────────────────
    private ErrorResponse buildError(HttpStatus status, String error,
                                     String message, String path) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private ErrorResponse.ValidationError mapFieldError(FieldError fieldError) {
        return ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}