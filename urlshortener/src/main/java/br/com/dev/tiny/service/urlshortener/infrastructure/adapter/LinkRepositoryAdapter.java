package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import br.com.dev.tiny.service.urlshortener.infrastructure.db.mongo.LinkDocument;
import br.com.dev.tiny.service.urlshortener.infrastructure.db.mongo.LinkMongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class LinkRepositoryAdapter implements LinkRepositoryPort {
    
    private final LinkMongoRepository mongoRepository;
    
    public LinkRepositoryAdapter(LinkMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }
    
    @Override
    public Link save(Link link) {
        LinkDocument document = toDocument(link);
        LinkDocument saved = mongoRepository.save(document);
        return toEntity(saved);
    }
    
    @Override
    public Optional<Link> findByCodeAndActive(String code) {
        return mongoRepository.findByCodeAndDeletedFalse(code)
                .map(this::toEntity);
    }
    
    @Override
    public Page<Link> findByUserIdAndActive(String userId, Pageable pageable) {
        return mongoRepository.findAllByUserIdAndDeletedFalse(userId, pageable)
                .map(this::toEntity);
    }
    
    @Override
    public long countByUserIdAndCreatedToday(String userId) {
        long startOfDay = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
                .atStartOfDay(ZoneId.of("America/Sao_Paulo"))
                .toInstant().toEpochMilli();
        return mongoRepository.countByUserIdAndCreatedAtGreaterThanEqual(userId, startOfDay);
    }
    
    private LinkDocument toDocument(Link link) {
        return new LinkDocument(
                link.getId(),
                link.getCode(),
                link.getUserId(),
                link.getOriginalUrl(),
                link.getCreatedAt().toEpochMilli(),
                link.getUpdatedAt().toEpochMilli(),
                link.isDeleted(),
                link.getVisits()
        );
    }
    
    private Link toEntity(LinkDocument document) {
        return new Link(
                document.id(),
                document.code(),
                document.userId(),
                document.originalUrl(),
                Instant.ofEpochMilli(document.createdAt()),
                Instant.ofEpochMilli(document.updatedAt()),
                document.deleted(),
                document.visits()
        );
    }
}
