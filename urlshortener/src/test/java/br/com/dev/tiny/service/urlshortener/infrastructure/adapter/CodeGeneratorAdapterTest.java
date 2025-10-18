package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes CodeGeneratorAdapter")
class CodeGeneratorAdapterTest {

    @Mock
    private LinkRepositoryPort linkRepository;

    private CodeGeneratorAdapter codeGeneratorAdapter;

    @BeforeEach
    void setUp() {
        codeGeneratorAdapter = new CodeGeneratorAdapter(linkRepository);
    }

    @Test
    @DisplayName("Deve gerar código único com tamanho correto")
    void shouldGenerateUniqueCodeWithCorrectLength() {
        when(linkRepository.findByCodeAndActive(anyString())).thenReturn(Optional.empty());

        String code = codeGeneratorAdapter.generateUniqueCode();

        assertNotNull(code);
        assertEquals(7, code.length());
        assertTrue(code.matches("[a-zA-Z0-9]+"));
    }

    @Test
    @DisplayName("Deve gerar códigos diferentes em múltiplas chamadas")
    void shouldGenerateDifferentCodesOnMultipleCalls() {
        when(linkRepository.findByCodeAndActive(anyString())).thenReturn(Optional.empty());

        String code1 = codeGeneratorAdapter.generateUniqueCode();
        String code2 = codeGeneratorAdapter.generateUniqueCode();
        String code3 = codeGeneratorAdapter.generateUniqueCode();

        assertNotEquals(code1, code2);
        assertNotEquals(code2, code3);
        assertNotEquals(code1, code3);
    }

    @Test
    @DisplayName("Deve gerar códigos apenas com caracteres alfanuméricos")
    void shouldGenerateCodesWithOnlyAlphanumericCharacters() {
        when(linkRepository.findByCodeAndActive(anyString())).thenReturn(Optional.empty());

        for (int i = 0; i < 10; i++) {
            String code = codeGeneratorAdapter.generateUniqueCode();
            
            assertTrue(code.matches("^[a-zA-Z0-9]+$"), "Code should contain only alphanumeric characters: " + code);
        }
    }
}
