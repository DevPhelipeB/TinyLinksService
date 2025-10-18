package br.com.dev.tiny.service.urlshortener.infrastructure.adapter;

import br.com.dev.tiny.service.urlshortener.domain.port.CodeGeneratorPort;
import br.com.dev.tiny.service.urlshortener.domain.port.LinkRepositoryPort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGeneratorAdapter implements CodeGeneratorPort {
    
    private final LinkRepositoryPort linkRepository;

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final SecureRandom RNG = new SecureRandom();
    
    public CodeGeneratorAdapter(LinkRepositoryPort linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    @Override
    public String generateUniqueCode() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String code = generateRandomCode(7);
            if (linkRepository.findByCodeAndActive(code).isEmpty()) {
                return code;
            }
        }

        String code = generateRandomCode(9);
        if (linkRepository.findByCodeAndActive(code).isEmpty()) {
            return code;
        }
        
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
