package br.com.dev.tiny.service.urlshortener.domain.usecase;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ListUserLinksUseCase {
    
    private final LinkRepositoryPort linkRepository;
    
    public ListUserLinksUseCase(LinkRepositoryPort linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    public Page<Link> execute(String userId, Pageable pageable) {
        return linkRepository.findByUserIdAndActive(userId, pageable);
    }
}
