package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import br.com.dev.tiny.service.urlshortener.infrastructure.db.mongo.LinkDocument;
import br.com.dev.tiny.service.urlshortener.infrastructure.db.mongo.LinkMongoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class LinkRepositoryAdapter implements LinkRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkRepositoryAdapter.class);
    private final LinkMongoRepository mongoRepository;
    
    public LinkRepositoryAdapter(LinkMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }
    
    @Override
    public Link save(Link link) {
        logger.debug("Salvando link no banco de dados: código={}, usuário={}", link.getCode(), link.getUserId());
        
        LinkDocument document = toDocument(link);
        LinkDocument saved = mongoRepository.save(document);
        Link result = toEntity(saved);
        
        logger.debug("Link salvo com sucesso: id={}, código={}", result.getId(), result.getCode());
        return result;
    }
    
    @Override
    public Optional<Link> findByCodeAndActive(String code) {
        logger.debug("Buscando link ativo por código: {}", code);
        
        Optional<Link> result = mongoRepository.findByCodeAndDeletedFalse(code)
                .map(this::toEntity);
        
        if (result.isPresent()) {
            logger.debug("Link ativo encontrado: código={}, usuário={}", code, result.get().getUserId());
        } else {
            logger.debug("Nenhum link ativo encontrado para código: {}", code);
        }
        
        return result;
    }
    
    @Override
    public Page<Link> findByUserIdAndActive(String userId, Pageable pageable) {
        logger.debug("Buscando links ativos para usuário: {}, página: {}, tamanho: {}", 
            userId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Link> result = mongoRepository.findAllByUserIdAndDeletedFalse(userId, pageable)
                .map(this::toEntity);
        
        logger.debug("Encontrados {} links ativos para usuário: {}, total: {}", 
            result.getNumberOfElements(), userId, result.getTotalElements());
        
        return result;
    }
    
    @Override
    public long countByUserIdAndCreatedToday(String userId) {
        logger.debug("Contando links criados hoje para usuário: {}", userId);
        
        long startOfDay = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
                .atStartOfDay(ZoneId.of("America/Sao_Paulo"))
                .toInstant().toEpochMilli();
        
        long count = mongoRepository.countByUserIdAndCreatedAtGreaterThanEqual(userId, startOfDay);
        logger.debug("Usuário {} criou {} links hoje", userId, count);
        
        return count;
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
