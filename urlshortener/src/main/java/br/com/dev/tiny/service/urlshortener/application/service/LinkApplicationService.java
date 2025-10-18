package br.com.dev.tiny.service.urlshortener.application.service;

import br.com.dev.tiny.service.urlshortener.application.dto.CreateLinkRequest;
import br.com.dev.tiny.service.urlshortener.application.dto.LinkResponse;
import br.com.dev.tiny.service.urlshortener.domain.entity.Link;
import br.com.dev.tiny.service.urlshortener.domain.usecase.CreateLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.DeleteLinkUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ListUserLinksUseCase;
import br.com.dev.tiny.service.urlshortener.domain.usecase.ResolveLinkUseCase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LinkApplicationService {
    
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
        String normalizedUserId = normalizeUserId(userId);
        Link link = createLinkUseCase.execute(request.originalUrl(), normalizedUserId);
        return LinkResponse.fromEntity(link, baseUrl);
    }
    
    public String resolveLink(String code) {
        return resolveLinkUseCase.execute(code);
    }
    
    public Page<LinkResponse> listUserLinks(String userId, Pageable pageable, String baseUrl) {
        String normalizedUserId = normalizeUserId(userId);
        Page<Link> links = listUserLinksUseCase.execute(normalizedUserId, pageable);
        return links.map(link -> LinkResponse.fromEntity(link, baseUrl));
    }
    
    public void deleteLink(String code, String userId) {
        String normalizedUserId = normalizeUserId(userId);
        deleteLinkUseCase.execute(code, normalizedUserId);
    }
    
    private String normalizeUserId(String userId) {
        return (userId == null || userId.isBlank()) ? "tester" : userId;
    }
}
