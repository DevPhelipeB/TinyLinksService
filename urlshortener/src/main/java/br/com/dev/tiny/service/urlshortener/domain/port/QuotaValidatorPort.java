package br.com.dev.tiny.service.urlshortener.domain.port;

public interface QuotaValidatorPort {
    
    void validateDailyQuota(String userId);
}
