package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.port.CodeGeneratorPort;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGeneratorAdapter implements CodeGeneratorPort {
    
    private static final Logger logger = LoggerFactory.getLogger(CodeGeneratorAdapter.class);

    private final LinkRepositoryPort linkRepository;

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final SecureRandom RNG = new SecureRandom();
    
    public CodeGeneratorAdapter(LinkRepositoryPort linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    @Override
    public String generateUniqueCode() {
        logger.debug("Iniciando geração de código único");
        
        for (int attempt = 0; attempt < 5; attempt++) {
            String code = generateRandomCode(7);
            logger.debug("Código de 7 caracteres gerado (tentativa {}): {}", attempt + 1, code);
            
            if (linkRepository.findByCodeAndActive(code).isEmpty()) {
                logger.debug("Código único gerado com sucesso: {}", code);
                return code;
            }
            logger.debug("Código {} já existe, tentando novamente", code);
        }

        logger.warn("Falha ao gerar código único de 7 caracteres após 5 tentativas, tentando código de 9 caracteres");
        String code = generateRandomCode(9);
        logger.debug("Código de 9 caracteres gerado: {}", code);
        
        if (linkRepository.findByCodeAndActive(code).isEmpty()) {
            logger.debug("Código único de 9 caracteres gerado com sucesso: {}", code);
            return code;
        }
        
        logger.error("Não foi possível gerar código único mesmo com 9 caracteres");
        throw new RuntimeException("Unable to generate unique code");
    }
    
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RNG.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
