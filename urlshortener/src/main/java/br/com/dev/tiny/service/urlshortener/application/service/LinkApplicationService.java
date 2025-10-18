package br.com.dev.tiny.service.urlshortener.application.service;

import br.com.dev.tiny.service.urlshortener.application.dto.CreateLinkRequest;
import br.com.dev.tiny.service.urlshortener.application.dto.LinkResponse;
import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.usecase.CreateLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.DeleteLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ListUserLinksUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ResolveLinkUseCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LinkApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkApplicationService.class);
    
    private final CreateLinkUseCase createLinkUseCase;

    private final ResolveLinkUseCase resolveLinkUseCase;

    private final ListUserLinksUseCase listUserLinksUseCase;

    private final DeleteLinkUseCase deleteLinkUseCase;
    
    public LinkApplicationService(CreateLinkUseCase createLinkUseCase, ResolveLinkUseCase resolveLinkUseCase, ListUserLinksUseCase listUserLinksUseCase, DeleteLinkUseCase deleteLinkUseCase) {
        this.createLinkUseCase = createLinkUseCase;
        this.resolveLinkUseCase = resolveLinkUseCase;
        this.listUserLinksUseCase = listUserLinksUseCase;
        this.deleteLinkUseCase = deleteLinkUseCase;
    }
    
    public LinkResponse createLink(CreateLinkRequest request, String userId, String baseUrl) {
        logger.debug("Processando requisição de criação de link: urlOriginal={}, usuário={}", request.originalUrl(), userId);
        
        String normalizedUserId = normalizeUserId(userId);
        logger.debug("Usuário normalizado: {} -> {}", userId, normalizedUserId);
        
        Link link = createLinkUseCase.execute(request.originalUrl(), normalizedUserId);
        LinkResponse response = LinkResponse.fromEntity(link, baseUrl);
        
        logger.debug("Link criado com sucesso: código={}, urlCurta={}", response.code(), response.shortUrl());
        return response;
    }
    
    public String resolveLink(String code) {
        logger.debug("Processando requisição de resolução de link: código={}", code);
        
        String targetUrl = resolveLinkUseCase.execute(code);
        logger.debug("Link resolvido com sucesso: código={}, urlDestino={}", code, targetUrl);
        
        return targetUrl;
    }
    
    public Page<LinkResponse> listUserLinks(String userId, Pageable pageable, String baseUrl) {
        logger.debug("Processando requisição de listagem de links do usuário: usuário={}, página={}, tamanho={}", 
            userId, pageable.getPageNumber(), pageable.getPageSize());
        
        String normalizedUserId = normalizeUserId(userId);
        logger.debug("Usuário normalizado: {} -> {}", userId, normalizedUserId);
        
        Page<Link> links = listUserLinksUseCase.execute(normalizedUserId, pageable);
        Page<LinkResponse> response = links.map(link -> LinkResponse.fromEntity(link, baseUrl));
        
        logger.debug("Links do usuário listados com sucesso: usuário={}, totalElementos={}, totalPaginas={}", 
            normalizedUserId, response.getTotalElements(), response.getTotalPages());
        
        return response;
    }
    
    public void deleteLink(String code, String userId) {
        logger.debug("Processando requisição de deleção de link: código={}, usuário={}", code, userId);
        
        String normalizedUserId = normalizeUserId(userId);
        logger.debug("Usuário normalizado: {} -> {}", userId, normalizedUserId);
        
        deleteLinkUseCase.execute(code, normalizedUserId);
        logger.debug("Link deletado com sucesso: código={}, usuário={}", code, normalizedUserId);
    }
    
    private String normalizeUserId(String userId) {
        return (userId == null || userId.isBlank()) ? "tester" : userId;
    }
}
