package br.com.dev.tiny.service.urlshortener.domain.usecase;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class ResolveLinkUseCase {
    
    private final LinkRepositoryPort linkRepository;
    
    public ResolveLinkUseCase(LinkRepositoryPort linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    public String execute(String code) {
        Link link = linkRepository.findByCodeAndActive(code)
                .orElseThrow(() -> new LinkNotFoundException("Link not found: " + code));

        Link updatedLink = link.incrementVisits();
        linkRepository.save(updatedLink);
        
        return link.getOriginalUrl();
    }
    
    public static class LinkNotFoundException extends RuntimeException {
        public LinkNotFoundException(String message) {
            super(message);
        }
    }
}
