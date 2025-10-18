package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import br.com.dev.tiny.service.urlshortener.domain.port.QuotaValidatorPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuotaValidatorAdapter implements QuotaValidatorPort {
    
    private static final Logger logger = LoggerFactory.getLogger(QuotaValidatorAdapter.class);

    private final LinkRepositoryPort linkRepository;

    // TODO - Ajusta em properties
    @Value("${tinylinks.quota.perUserPerDay:200}")
    private int perUserPerDay;
    
    public QuotaValidatorAdapter(LinkRepositoryPort linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    @Override
    public void validateDailyQuota(String userId) {
        logger.debug("Validando cota diária para usuário: {}", userId);
        
        long count = linkRepository.countByUserIdAndCreatedToday(userId);
        logger.debug("Contagem diária atual para usuário {}: {}/{}", userId, count, perUserPerDay);
        
        if (count >= perUserPerDay) {
            logger.warn("Quota diária excedida para usuário: {} - Atual: {}, Limite: {}", userId, count, perUserPerDay);
            throw new QuotaExceededException("Daily quota exceeded for user: " + userId);
        }
        
        logger.debug("Validação de cota diária aprovada para usuário: {} - Atual: {}/{}", userId, count, perUserPerDay);
    }
    
    public static class QuotaExceededException extends RuntimeException {
        public QuotaExceededException(String message) {
            super(message);
        }
    }
}
