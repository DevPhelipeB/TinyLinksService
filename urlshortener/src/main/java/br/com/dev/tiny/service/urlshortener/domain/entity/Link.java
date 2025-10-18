package br.com.dev.tiny.service.urlshortener.domain.entity;

import java.time.Instant;

public class Link {
    
    private final String id;
    private final String code;
    private final String userId;
    private final String originalUrl;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final boolean deleted;
    private final long visits;
    
    public Link(String id, String code, String userId, String originalUrl, Instant createdAt, Instant updatedAt, boolean deleted, long visits) {
        this.id = id;
        this.code = code;
        this.userId = userId;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
        this.visits = visits;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getUserId() { return userId; }
    public String getOriginalUrl() { return originalUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return deleted; }
    public long getVisits() { return visits; }

    public boolean belongsToUser(String userId) {
        return this.userId.equals(userId);
    }

    public boolean isActive() {
        return !deleted;
    }

    public Link incrementVisits() {
        return new Link(id, code, userId, originalUrl, createdAt, Instant.now(), deleted, visits + 1);
    }

    public Link markAsDeleted() {
        return new Link(id, code, userId, originalUrl, createdAt, Instant.now(), true, visits);
    }

    public boolean wasCreatedToday() {
        Instant startOfDay = Instant.now().atZone(java.time.ZoneId.of("America/Sao_Paulo"))
                .toLocalDate().atStartOfDay(java.time.ZoneId.of("America/Sao_Paulo")).toInstant();
        return createdAt.isAfter(startOfDay);
    }
}
