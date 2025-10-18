package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import br.com.dev.tiny.service.urlshortener.infrastructure.config.QuotaProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

@DisplayName("Testes QuotaValidatorAdapter")
class QuotaValidatorAdapterTest {

    @Mock
    private LinkRepositoryPort linkRepository;

    @Mock
    private QuotaProperties quotaProperties;

    private QuotaValidatorAdapter quotaValidatorAdapter;

    @BeforeEach
    void setUp() {
        when(quotaProperties.getPerUserPerDay()).thenReturn(200);
        quotaValidatorAdapter = new QuotaValidatorAdapter(linkRepository, quotaProperties);
    }

    @Test
    @DisplayName("Deve validar quota com sucesso quando abaixo do limite")
    void shouldValidateQuotaSuccessfullyWhenUnderLimit() {
        String userId = "user1";
        long currentCount = 50;

        when(linkRepository.countByUserIdAndCreatedToday(userId)).thenReturn(currentCount);

        assertDoesNotThrow(() -> quotaValidatorAdapter.validateDailyQuota(userId));

        verify(linkRepository).countByUserIdAndCreatedToday(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando quota é excedida")
    void shouldThrowExceptionWhenQuotaIsExceeded() {
        String userId = "user1";
        long currentCount = 250;

        when(linkRepository.countByUserIdAndCreatedToday(userId)).thenReturn(currentCount);

        QuotaValidatorAdapter.QuotaExceededException exception = assertThrows(
                QuotaValidatorAdapter.QuotaExceededException.class, () -> {
                    quotaValidatorAdapter.validateDailyQuota(userId);
                });

        assertEquals("Daily quota exceeded for user: " + userId, exception.getMessage());
        verify(linkRepository).countByUserIdAndCreatedToday(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando quota está no limite")
    void shouldThrowExceptionWhenQuotaIsAtLimit() {
        String userId = "user1";
        long currentCount = 200;

        when(linkRepository.countByUserIdAndCreatedToday(userId)).thenReturn(currentCount);

        QuotaValidatorAdapter.QuotaExceededException exception = assertThrows(
                QuotaValidatorAdapter.QuotaExceededException.class, () -> {
                    quotaValidatorAdapter.validateDailyQuota(userId);
                });

        assertEquals("Daily quota exceeded for user: " + userId, exception.getMessage());
        verify(linkRepository).countByUserIdAndCreatedToday(userId);
    }

    @Test
    @DisplayName("Deve validar quota com sucesso quando no limite máximo permitido")
    void shouldValidateQuotaSuccessfullyWhenAtMaxAllowedLimit() {
        String userId = "user1";
        long currentCount = 199;

        when(linkRepository.countByUserIdAndCreatedToday(userId)).thenReturn(currentCount);

        assertDoesNotThrow(() -> quotaValidatorAdapter.validateDailyQuota(userId));

        verify(linkRepository).countByUserIdAndCreatedToday(userId);
    }

    @Test
    @DisplayName("Deve validar quota com sucesso quando nenhum link foi criado")
    void shouldValidateQuotaSuccessfullyWhenNoLinksCreated() {
        String userId = "user1";
        long currentCount = 0;

        when(linkRepository.countByUserIdAndCreatedToday(userId)).thenReturn(currentCount);

        assertDoesNotThrow(() -> quotaValidatorAdapter.validateDailyQuota(userId));

        verify(linkRepository).countByUserIdAndCreatedToday(userId);
    }
}
