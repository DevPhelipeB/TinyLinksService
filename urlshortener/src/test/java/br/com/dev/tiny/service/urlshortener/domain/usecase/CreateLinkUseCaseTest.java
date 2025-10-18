package br.com.dev.tiny.service.urlshortener.domain.usecase;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.CodeGeneratorPort;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import br.com.dev.tiny.service.urlshortener.domain.port.QuotaValidatorPort;
import br.com.dev.tiny.service.urlshortener.infrastructure.adapter.QuotaValidatorAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes CreateLinkUseCase")
class CreateLinkUseCaseTest {

    @Mock
    private LinkRepositoryPort linkRepository;

    @Mock
    private CodeGeneratorPort codeGenerator;

    @Mock
    private QuotaValidatorPort quotaValidator;

    private CreateLinkUseCase createLinkUseCase;

    @BeforeEach
    void setUp() {
        createLinkUseCase = new CreateLinkUseCase(linkRepository, codeGenerator, quotaValidator);
    }

    @Test
    @DisplayName("Deve criar link com sucesso")
    void shouldCreateLinkSuccessfully() {
        String originalUrl = "https://github.com/DevPhelipeB";
        String userId = "user1";
        String generatedCode = "abc123";
        Link expectedLink = new Link("123", generatedCode, userId, originalUrl, Instant.now(), Instant.now(), false, 0);

        when(codeGenerator.generateUniqueCode()).thenReturn(generatedCode);
        when(linkRepository.save(any(Link.class))).thenReturn(expectedLink);

        Link result = createLinkUseCase.execute(originalUrl, userId);

        assertNotNull(result);
        assertEquals(generatedCode, result.getCode());
        assertEquals(userId, result.getUserId());
        assertEquals(originalUrl, result.getOriginalUrl());
        assertFalse(result.isDeleted());
        assertEquals(0, result.getVisits());

        verify(quotaValidator).validateDailyQuota(userId);
        verify(codeGenerator).generateUniqueCode();
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando quota é excedida")
    void shouldThrowExceptionWhenQuotaIsExceeded() {
        String originalUrl = "https://github.com/DevPhelipeB";
        String userId = "user1";

        doThrow(new QuotaValidatorAdapter.QuotaExceededException("Quota exceeded")).when(quotaValidator).validateDailyQuota(userId);

        assertThrows(QuotaValidatorAdapter.QuotaExceededException.class, () -> {
            createLinkUseCase.execute(originalUrl, userId);
        });

        verify(quotaValidator).validateDailyQuota(userId);
        verify(codeGenerator, never()).generateUniqueCode();
        verify(linkRepository, never()).save(any(Link.class));
    }
}
