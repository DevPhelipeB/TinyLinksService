package br.com.dev.tiny.service.urlshortener.infrastructure.db.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkMongoRepository extends MongoRepository<LinkDocument, String> {
    
    Optional<LinkDocument> findByCodeAndDeletedFalse(String code);
    
    long countByUserIdAndCreatedAtGreaterThanEqual(String userId, long startOfDayMillis);
    
    Page<LinkDocument> findAllByUserIdAndDeletedFalse(String userId, Pageable pageable);
}
