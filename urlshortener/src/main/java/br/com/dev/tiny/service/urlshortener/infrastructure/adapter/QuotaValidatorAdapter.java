package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import br.com.dev.tiny.service.urlshortener.domain.port.QuotaValidatorPort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuotaValidatorAdapter implements QuotaValidatorPort {
    
    private final LinkRepositoryPort linkRepository;

    // TODO - Ajusta em properties
    @Value("${tinylinks.quota.perUserPerDay:200}")
    private int perUserPerDay;
    
    public QuotaValidatorAdapter(LinkRepositoryPort linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    @Override
    public void validateDailyQuota(String userId) {
        long count = linkRepository.countByUserIdAndCreatedToday(userId);
        if (count >= perUserPerDay) {
            throw new QuotaExceededException("Daily quota exceeded for user: " + userId);
        }
    }
    
    public static class QuotaExceededException extends RuntimeException {
        public QuotaExceededException(String message) {
            super(message);
        }
    }
}
