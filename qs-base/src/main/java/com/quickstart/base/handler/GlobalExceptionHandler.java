package com.quickstart.base.handler;

import com.quickstart.base.domain.ResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDTO<Void> handleValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数校验失败" : fieldError.getDefaultMessage();
        log.warn("Validation failed: {}", message);
        return ResponseDTO.userErrorParam(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseDTO<Void> handleConstraintException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseDTO.userErrorParam(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDTO<Void> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseDTO.userErrorParam(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseDTO<Void> handleAuthenticationException(AuthenticationCredentialsNotFoundException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseDTO.error(com.quickstart.base.domain.ErrorCode.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseDTO<Void> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseDTO.error(com.quickstart.base.domain.ErrorCode.FORBIDDEN, "没有访问权限");
    }
}
