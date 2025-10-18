package br.com.dev.tiny.service.urlshortener.infrastructure.web.controller;

import br.com.dev.tiny.service.urlshortener.application.dto.CreateLinkRequest;
import br.com.dev.tiny.service.urlshortener.application.dto.LinkResponse;
import br.com.dev.tiny.service.urlshortener.application.service.LinkApplicationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes LinkController")
class LinkControllerTest {

    @Mock
    private LinkApplicationService applicationService;

    private LinkController linkController;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        linkController = new LinkController(applicationService);
        request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8077);
        request.setContextPath("/tinyapp");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("Deve criar link com sucesso")
    void shouldCreateLinkSuccessfully() {
        CreateLinkRequest createRequest = new CreateLinkRequest("https://github.com/DevPhelipeB");
        String userId = "user1";
        LinkResponse expectedResponse = new LinkResponse("abc123", "http://localhost:8077/tinyapp/api/v1/r/abc123", "https://github.com/DevPhelipeB", 0);

        when(applicationService.createLink(createRequest, userId, "http://localhost:8077")).thenReturn(expectedResponse);

        LinkResponse result = linkController.createLink(createRequest, userId, request);

        assertNotNull(result);
        assertEquals("abc123", result.code());
        assertEquals("http://localhost:8077/tinyapp/api/v1/r/abc123", result.shortUrl());
        verify(applicationService).createLink(createRequest, userId, "http://localhost:8077");
    }

    @Test
    @DisplayName("Deve listar links do usuário com sucesso")
    void shouldListUserLinksSuccessfully() {
        String userId = "user1";
        int page = 0;
        int size = 10;
        LinkResponse linkResponse = new LinkResponse("abc123", "http://localhost:8077/tinyapp/api/v1/r/abc123", "https://github.com/DevPhelipeB", 5);
        Page<LinkResponse> expectedPage = new PageImpl<>(List.of(linkResponse));

        when(applicationService.listUserLinks(userId, PageRequest.of(page, size), "http://localhost:8077")).thenReturn(expectedPage);

        Page<LinkResponse> result = linkController.listUserLinks(userId, page, size, request);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("abc123", result.getContent().get(0).code());
        verify(applicationService).listUserLinks(userId, PageRequest.of(page, size), "http://localhost:8077");
    }

    @Test
    @DisplayName("Deve redirecionar para URL original com sucesso")
    void shouldRedirectToOriginalUrlSuccessfully() {
        String code = "abc123";
        String originalUrl = "https://github.com/DevPhelipeB";

        when(applicationService.resolveLink(code)).thenReturn(originalUrl);

        ResponseEntity<Void> result = linkController.resolveLink(code);

        assertNotNull(result);
        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertTrue(result.getHeaders().getLocation().toString().contains(originalUrl));
        verify(applicationService).resolveLink(code);
    }

    @Test
    @DisplayName("Deve deletar link com sucesso")
    void shouldDeleteLinkSuccessfully() {
        String code = "abc123";
        String userId = "user1";

        ResponseEntity<Void> result = linkController.deleteLink(code, userId);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(applicationService).deleteLink(code, userId);
    }

    @Test
    @DisplayName("Deve lidar com fallback de escrita corretamente")
    void shouldHandleWriteFallbackCorrectly() {
        CreateLinkRequest createRequest = new CreateLinkRequest("https://github.com/DevPhelipeB");
        String userId = "user1";
        io.github.resilience4j.ratelimiter.RequestNotPermitted ex = mock(io.github.resilience4j.ratelimiter.RequestNotPermitted.class);
        
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            linkController.writeFallback(createRequest, userId, request, ex);
        });
    }

    @Test
    @DisplayName("Deve lidar com fallback de leitura corretamente")
    void shouldHandleReadFallbackCorrectly() {
        String userId = "user1";
        int page = 0;
        int size = 10;
        io.github.resilience4j.ratelimiter.RequestNotPermitted ex = mock(io.github.resilience4j.ratelimiter.RequestNotPermitted.class);

        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            linkController.readFallbackPage(userId, page, size, request, ex);
        });
    }
}
