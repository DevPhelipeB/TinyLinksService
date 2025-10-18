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
@DisplayName("Testes DeleteLinkUseCase")
class DeleteLinkUseCaseTest {

    @Mock
    private LinkRepositoryPort linkRepository;

    private DeleteLinkUseCase deleteLinkUseCase;

    @BeforeEach
    void setUp() {
        deleteLinkUseCase = new DeleteLinkUseCase(linkRepository);
    }

    @Test
    @DisplayName("Deve deletar link com sucesso quando usuário é o dono")
    void shouldDeleteLinkSuccessfullyWhenUserOwnsIt() {
        String code = "abc123";
        String userId = "user1";
        Link link = new Link("123", code, userId, "https://github.com/DevPhelipeB", Instant.now(), Instant.now(), false, 5);

        when(linkRepository.findByCodeAndActive(code)).thenReturn(Optional.of(link));
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        assertDoesNotThrow(() -> deleteLinkUseCase.execute(code, userId));

        verify(linkRepository).findByCodeAndActive(code);
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando link não for encontrado")
    void shouldThrowExceptionWhenLinkNotFound() {
        String code = "nonexistent";
        String userId = "user1";

        when(linkRepository.findByCodeAndActive(code)).thenReturn(Optional.empty());

        DeleteLinkUseCase.LinkNotFoundException exception = assertThrows(
                DeleteLinkUseCase.LinkNotFoundException.class, () -> {
                    deleteLinkUseCase.execute(code, userId);
                });

        assertEquals("Link not found: nonexistent", exception.getMessage());
        verify(linkRepository).findByCodeAndActive(code);
        verify(linkRepository, never()).save(any(Link.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é dono do link")
    void shouldThrowExceptionWhenUserDoesNotOwnLink() {
        String code = "abc123";
        String userId = "user1";
        String differentUserId = "user2";
        Link link = new Link("123", code, differentUserId, "https://github.com/DevPhelipeB", Instant.now(), Instant.now(), false, 5);

        when(linkRepository.findByCodeAndActive(code)).thenReturn(Optional.of(link));

        DeleteLinkUseCase.UnauthorizedAccessException exception = assertThrows(
                DeleteLinkUseCase.UnauthorizedAccessException.class, () -> {
                    deleteLinkUseCase.execute(code, userId);
                });

        assertEquals("User does not own this link", exception.getMessage());
        verify(linkRepository).findByCodeAndActive(code);
        verify(linkRepository, never()).save(any(Link.class));
    }
}
