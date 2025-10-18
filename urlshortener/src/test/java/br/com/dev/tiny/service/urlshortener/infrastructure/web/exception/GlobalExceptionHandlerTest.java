package br.com.dev.tiny.service.urlshortener.infrastructure.web.exception;

import br.com.dev.tiny.service.urlshortener.domain.usecase.DeleteLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ResolveLinkUseCase;
import br.com.dev.tiny.service.urlshortener.infrastructure.adapter.QuotaValidatorAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve lidar com LinkNotFoundException corretamente")
    void shouldHandleLinkNotFoundExceptionCorrectly() {
        ResolveLinkUseCase.LinkNotFoundException exception = new ResolveLinkUseCase.LinkNotFoundException("Link not found: abc123");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleLinkNotFound(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Link not found: abc123", body.get("message"));
        assertEquals("Link not found", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Deve lidar com UnauthorizedAccessException corretamente")
    void shouldHandleUnauthorizedAccessExceptionCorrectly() {
        DeleteLinkUseCase.UnauthorizedAccessException exception = new DeleteLinkUseCase.UnauthorizedAccessException("User does not own this link");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUnauthorizedAccess(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("User does not own this link", body.get("message"));
        assertEquals("Unauthorized access", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Deve lidar com QuotaExceededException corretamente")
    void shouldHandleQuotaExceededExceptionCorrectly() {
        QuotaValidatorAdapter.QuotaExceededException exception = new QuotaValidatorAdapter.QuotaExceededException("Daily quota exceeded. Current: 250, Limit: 200");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleQuotaExceeded(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Daily quota exceeded. Current: 250, Limit: 200", body.get("message"));
        assertEquals("Quota exceeded", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Deve lidar com QuotaExceededException com tipo de erro correto")
    void shouldHandleQuotaExceededExceptionWithCorrectErrorType() {
        QuotaValidatorAdapter.QuotaExceededException exception = new QuotaValidatorAdapter.QuotaExceededException("Daily quota exceeded. Current: 250, Limit: 200");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleQuotaExceeded(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Daily quota exceeded. Current: 250, Limit: 200", body.get("message"));
        assertEquals("Quota exceeded", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }
}
