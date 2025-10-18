package br.com.dev.tiny.service.urlshortener.application.service;

import br.com.dev.tiny.service.urlshortener.application.dto.CreateLinkRequest;
import br.com.dev.tiny.service.urlshortener.application.dto.LinkResponse;
import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.usecase.CreateLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.DeleteLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ListUserLinksUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ResolveLinkUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes LinkApplicationService")
class LinkApplicationServiceTest {

    @Mock
    private CreateLinkUseCase createLinkUseCase;

    @Mock
    private ResolveLinkUseCase resolveLinkUseCase;

    @Mock
    private ListUserLinksUseCase listUserLinksUseCase;

    @Mock
    private DeleteLinkUseCase deleteLinkUseCase;

    private LinkApplicationService linkApplicationService;

    @BeforeEach
    void setUp() {
        linkApplicationService = new LinkApplicationService(createLinkUseCase, resolveLinkUseCase, listUserLinksUseCase, deleteLinkUseCase);
    }

    @Test
    @DisplayName("Deve criar link com sucesso")
    void shouldCreateLinkSuccessfully() {
        CreateLinkRequest request = new CreateLinkRequest("https://github.com/DevPhelipeB");
        String userId = "user1";
        String baseUrl = "http://localhost:8077";
        Link link = new Link("123", "abc123", userId, request.originalUrl(), Instant.now(), Instant.now(), false, 0);

        when(createLinkUseCase.execute(request.originalUrl(), userId)).thenReturn(link);

        LinkResponse result = linkApplicationService.createLink(request, userId, baseUrl);

        assertNotNull(result);
        assertEquals("abc123", result.code());
        assertEquals("http://localhost:8077/tinyapp/api/v1/r/abc123", result.shortUrl());
        assertEquals("https://github.com/DevPhelipeB", result.originalUrl());
        assertEquals(0, result.visits());

        verify(createLinkUseCase).execute(request.originalUrl(), userId);
    }

    @Test
    @DisplayName("Deve normalizar userId para tester quando nulo")
    void shouldNormalizeUserIdToTesterWhenNull() {
        CreateLinkRequest request = new CreateLinkRequest("https://github.com/DevPhelipeB");
        String baseUrl = "http://localhost:8077";
        Link link = new Link("123", "abc123", "tester", request.originalUrl(), Instant.now(), Instant.now(), false, 0);

        when(createLinkUseCase.execute(request.originalUrl(), "tester")).thenReturn(link);

        LinkResponse result = linkApplicationService.createLink(request, null, baseUrl);

        assertNotNull(result);
        verify(createLinkUseCase).execute(request.originalUrl(), "tester");
    }

    @Test
    @DisplayName("Deve resolver link com sucesso")
    void shouldResolveLinkSuccessfully() {
        String code = "abc123";
        String expectedUrl = "https://github.com/DevPhelipeB";

        when(resolveLinkUseCase.execute(code)).thenReturn(expectedUrl);

        String result = linkApplicationService.resolveLink(code);

        assertEquals(expectedUrl, result);
        verify(resolveLinkUseCase).execute(code);
    }

    @Test
    @DisplayName("Deve listar links do usuário com sucesso")
    void shouldListUserLinksSuccessfully() {
        String userId = "user1";
        String baseUrl = "http://localhost:8077";
        Pageable pageable = PageRequest.of(0, 10);
        Link link = new Link("123", "abc123", userId, "https://github.com/DevPhelipeB", Instant.now(), Instant.now(), false, 5);
        Page<Link> linkPage = new PageImpl<>(List.of(link));

        when(listUserLinksUseCase.execute(userId, pageable)).thenReturn(linkPage);

        Page<LinkResponse> result = linkApplicationService.listUserLinks(userId, pageable, baseUrl);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("abc123", result.getContent().get(0).code());
        verify(listUserLinksUseCase).execute(userId, pageable);
    }

    @Test
    @DisplayName("Deve deletar link com sucesso")
    void shouldDeleteLinkSuccessfully() {
        String code = "abc123";
        String userId = "user1";

        assertDoesNotThrow(() -> linkApplicationService.deleteLink(code, userId));

        verify(deleteLinkUseCase).execute(code, userId);
    }
}
