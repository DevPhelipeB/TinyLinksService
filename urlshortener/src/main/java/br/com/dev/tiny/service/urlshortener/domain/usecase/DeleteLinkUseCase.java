package br.com.dev.tiny.service.urlshortener.domain.usecase;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;

public class DeleteLinkUseCase {
    
    private final LinkRepositoryPort linkRepository;
    
    public DeleteLinkUseCase(LinkRepositoryPort linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    public void execute(String code, String userId) {
        Link link = linkRepository.findByCodeAndActive(code)
                .orElseThrow(() -> new LinkNotFoundException("Link not found: " + code));

        if (!link.belongsToUser(userId)) {
            throw new UnauthorizedAccessException("User does not own this link");
        }

        Link deletedLink = link.markAsDeleted();
        linkRepository.save(deletedLink);
    }
    
    public static class LinkNotFoundException extends RuntimeException {
        public LinkNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }
}
