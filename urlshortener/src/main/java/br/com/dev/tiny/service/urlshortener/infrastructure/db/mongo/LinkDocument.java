package br.com.dev.tiny.service.urlshortener.infrastructure.db.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("links")
@CompoundIndex(name = "user_created_idx", def = "{'userId':1, 'createdAt':1}")
public record LinkDocument(

    @Id String id,

    @Indexed(unique = true) String code,

    String userId,

    String originalUrl,

    long createdAt,

    long updatedAt,

    boolean deleted,

    long visits

) {}
