package br.com.dev.tiny.service.urlshortener.infrastructure.web.exception;

import br.com.dev.tiny.service.urlshortener.domain.usecase.DeleteLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ResolveLinkUseCase;
import br.com.dev.tiny.service.urlshortener.infrastructure.adapter.QuotaValidatorAdapter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResolveLinkUseCase.LinkNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLinkNotFound(ResolveLinkUseCase.LinkNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Link not found",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }
    
    @ExceptionHandler(DeleteLinkUseCase.LinkNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDeleteLinkNotFound(DeleteLinkUseCase.LinkNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Link not found",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }
    
    @ExceptionHandler(DeleteLinkUseCase.UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAccess(DeleteLinkUseCase.UnauthorizedAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "Unauthorized access",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }
    
    @ExceptionHandler(QuotaValidatorAdapter.QuotaExceededException.class)
    public ResponseEntity<Map<String, Object>> handleQuotaExceeded(QuotaValidatorAdapter.QuotaExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                        "error", "Quota exceeded",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }
}
