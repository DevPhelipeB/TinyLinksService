package br.com.dev.tiny.service.urlshortener.domain.usecase;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import br.com.dev.tiny.service.urlshortener.domain.port.CodeGeneratorPort;
import br.com.dev.tiny.service.urlshortener.domain.port.QuotaValidatorPort;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class CreateLinkUseCase {
    
    private final LinkRepositoryPort linkRepository;

    private final CodeGeneratorPort codeGenerator;

    private final QuotaValidatorPort quotaValidator;
    
    public CreateLinkUseCase(LinkRepositoryPort linkRepository, CodeGeneratorPort codeGenerator, QuotaValidatorPort quotaValidator) {
        this.linkRepository = linkRepository;
        this.codeGenerator = codeGenerator;
        this.quotaValidator = quotaValidator;
    }
    
    public Link execute(String originalUrl, String userId) {
        quotaValidator.validateDailyQuota(userId);

        String code = codeGenerator.generateUniqueCode();

        Instant now = Instant.now();
        Link link = new Link(null, code, userId, originalUrl, now, now, false, 0);

        return linkRepository.save(link);
    }
}
