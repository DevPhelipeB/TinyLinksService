package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes LinkRepositoryAdapter")
class LinkRepositoryAdapterTest {

    @Mock
    private Object mongoRepository;

    private LinkRepositoryAdapter linkRepositoryAdapter;

    @BeforeEach
    void setUp() {
        linkRepositoryAdapter = new LinkRepositoryAdapter(null);
    }

    @Test
    @DisplayName("Deve criar adapter com sucesso")
    void shouldCreateAdapterSuccessfully() {
        LinkRepositoryAdapter adapter = new LinkRepositoryAdapter(null);

        assertNotNull(adapter);
    }

    @Test
    @DisplayName("Deve lidar com entidade link corretamente")
    void shouldHandleLinkEntityCorrectly() {
        Instant now = Instant.now();
        Link link = new Link("123", "abc123", "user1", "https://github.com/DevPhelipeB", now, now, false, 0);

        assertNotNull(link);
        assertEquals("123", link.getId());
        assertEquals("abc123", link.getCode());
        assertEquals("user1", link.getUserId());
        assertEquals("https://github.com/DevPhelipeB", link.getOriginalUrl());
        assertFalse(link.isDeleted());
        assertEquals(0, link.getVisits());
    }
}
