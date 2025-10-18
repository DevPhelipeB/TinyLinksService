package br.com.dev.tiny.service.urlshortener.domain.usecase;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes ResolveLinkUseCase")
class ResolveLinkUseCaseTest {

    @Mock
    private LinkRepositoryPort linkRepository;

    private ResolveLinkUseCase resolveLinkUseCase;

    @BeforeEach
    void setUp() {
        resolveLinkUseCase = new ResolveLinkUseCase(linkRepository);
    }

    @Test
    @DisplayName("Deve resolver link com sucesso")
    void shouldResolveLinkSuccessfully() {
        String code = "abc123";
        String originalUrl = "https://github.com/DevPhelipeB";
        Link link = new Link("123", code, "user1", originalUrl, Instant.now(), Instant.now(), false, 5);
        Link updatedLink = new Link("123", code, "user1", originalUrl, Instant.now(), Instant.now(), false, 6);

        when(linkRepository.findByCodeAndActive(code)).thenReturn(Optional.of(link));
        when(linkRepository.save(any(Link.class))).thenReturn(updatedLink);

        String result = resolveLinkUseCase.execute(code);

        assertEquals(originalUrl, result);
        verify(linkRepository).findByCodeAndActive(code);
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando link não for encontrado")
    void shouldThrowExceptionWhenLinkNotFound() {
        String code = "nonexistent";

        when(linkRepository.findByCodeAndActive(code)).thenReturn(Optional.empty());

        ResolveLinkUseCase.LinkNotFoundException exception = assertThrows(
                ResolveLinkUseCase.LinkNotFoundException.class, () -> {
                    resolveLinkUseCase.execute(code);
                });

        assertEquals("Link not found: nonexistent", exception.getMessage());
        verify(linkRepository).findByCodeAndActive(code);
        verify(linkRepository, never()).save(any(Link.class));
    }
}
