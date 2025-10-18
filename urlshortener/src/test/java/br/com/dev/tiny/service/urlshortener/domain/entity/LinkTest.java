package br.com.dev.tiny.service.urlshortener.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Entidade Link")
class LinkTest {

    @Test
    @DisplayName("Deve criar link com todas as propriedades")
    void shouldCreateLinkWithAllProperties() {
        String id = "123";
        String code = "abc123";
        String userId = "user1";
        String originalUrl = "https://github.com/DevPhelipeB";
        Instant now = Instant.now();
        boolean deleted = false;
        long visits = 0;

        Link link = new Link(id, code, userId, originalUrl, now, now, deleted, visits);

        assertEquals(id, link.getId());
        assertEquals(code, link.getCode());
        assertEquals(userId, link.getUserId());
        assertEquals(originalUrl, link.getOriginalUrl());
        assertEquals(now, link.getCreatedAt());
        assertEquals(now, link.getUpdatedAt());
        assertEquals(deleted, link.isDeleted());
        assertEquals(visits, link.getVisits());
    }

    @Test
    @DisplayName("Deve retornar true quando link pertence ao usuário")
    void shouldReturnTrueWhenLinkBelongsToUser() {
        Link link = createTestLink();
        String userId = "user1";

        boolean belongsToUser = link.belongsToUser(userId);

        assertTrue(belongsToUser);
    }

    @Test
    @DisplayName("Deve retornar false quando link não pertence ao usuário")
    void shouldReturnFalseWhenLinkDoesNotBelongToUser() {
        Link link = createTestLink();
        String differentUserId = "user2";

        boolean belongsToUser = link.belongsToUser(differentUserId);

        assertFalse(belongsToUser);
    }

    @Test
    @DisplayName("Deve retornar true quando link está ativo")
    void shouldReturnTrueWhenLinkIsActive() {
        Link link = createTestLink();

        boolean isActive = link.isActive();

        assertTrue(isActive);
    }

    @Test
    @DisplayName("Deve retornar false quando link está deletado")
    void shouldReturnFalseWhenLinkIsDeleted() {
        Link link = new Link("123", "abc123", "user1", "https://github.com/DevPhelipeB", Instant.now(), Instant.now(), true, 0);

        boolean isActive = link.isActive();

        assertFalse(isActive);
    }

    @Test
    @DisplayName("Deve incrementar visitas corretamente")
    void shouldIncrementVisitsCorrectly() {
        Link link = createTestLink();
        long initialVisits = link.getVisits();

        Link updatedLink = link.incrementVisits();

        assertEquals(initialVisits + 1, updatedLink.getVisits());
    }

    @Test
    @DisplayName("Deve marcar link como deletado")
    void shouldMarkLinkAsDeleted() {
        Link link = createTestLink();

        Link deletedLink = link.markAsDeleted();

        assertTrue(deletedLink.isDeleted());
    }

    @Test
    @DisplayName("Deve retornar true quando link foi criado hoje")
    void shouldReturnTrueWhenLinkWasCreatedToday() {
        Link link = createTestLink();

        boolean wasCreatedToday = link.wasCreatedToday();

        assertTrue(wasCreatedToday);
    }

    private Link createTestLink() {
        return new Link("123", "abc123", "user1", "https://github.com/DevPhelipeB", Instant.now(), Instant.now(), false, 0);
    }
}
