package br.com.dev.tiny.service.urlshortener.domain.port;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LinkRepositoryPort {
    
    Link save(Link link);
    
    Optional<Link> findByCodeAndActive(String code);
    
    Page<Link> findByUserIdAndActive(String userId, Pageable pageable);
    
    long countByUserIdAndCreatedToday(String userId);
}
