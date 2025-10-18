package br.com.dev.tiny.service.urlshortener.infrastructure.web.controller;

import br.com.dev.tiny.service.urlshortener.application.dto.CreateLinkRequest;
import br.com.dev.tiny.service.urlshortener.application.dto.LinkResponse;
import br.com.dev.tiny.service.urlshortener.application.service.LinkApplicationService;
import br.com.dev.tiny.service.urlshortener.infrastructure.config.UrlUtils;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1")
public class LinkController {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkController.class);

    private final LinkApplicationService applicationService;
    
    public LinkController(LinkApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    

    @PostMapping("/links")
    @RateLimiter(name = "writeLimiter", fallbackMethod = "writeFallback")
    public LinkResponse createLink(@Valid @RequestBody CreateLinkRequest request, @RequestHeader(value = "X-User-Id", required = false) String userId, HttpServletRequest httpRequest) {
        logger.info("Criando novo link para usuário: {} com URL: {}", userId, request.originalUrl());
        
        try {
            String baseUrl = UrlUtils.buildBaseUrl(httpRequest);
            LinkResponse response = applicationService.createLink(request, userId, baseUrl);
            
            logger.info("Link criado com sucesso: código={}, urlCurta={}, usuário={}", 
                response.code(), response.shortUrl(), userId);
            
            return response;
        } catch (Exception e) {
            logger.error("Falha ao criar link para usuário: {} com URL: {}", userId, request.originalUrl(), e);
            throw e;
        }
    }
    
    public LinkResponse writeFallback(CreateLinkRequest request, String userId, HttpServletRequest httpRequest, RequestNotPermitted requestNotPermitted) {
        logger.warn("Limite de taxa de escrita excedido para usuário: {}", userId);
        throw new ResponseStatusException(TOO_MANY_REQUESTS, "Write rate exceeded");
    }

    @GetMapping("/links")
    @RateLimiter(name = "readLimiter", fallbackMethod = "readFallbackPage")
    public Page<LinkResponse> listUserLinks(@RequestHeader(value = "X-User-Id", required = false) String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest httpRequest) {
        logger.info("Listando links para usuário: {}, página: {}, tamanho: {}", userId, page, size);
        
        try {
            String baseUrl = UrlUtils.buildBaseUrl(httpRequest);
            Page<LinkResponse> response = applicationService.listUserLinks(userId, PageRequest.of(page, size), baseUrl);
            
            logger.info("Links listados com sucesso: usuário={}, totalElementos={}, totalPaginas={}", 
                userId, response.getTotalElements(), response.getTotalPages());
            
            return response;
        } catch (Exception e) {
            logger.error("Falha ao listar links para usuário: {}", userId, e);
            throw e;
        }
    }
    
    public Page<LinkResponse> readFallbackPage(String userId, int page, int size, HttpServletRequest httpRequest, RequestNotPermitted requestNotPermitted) {
        logger.warn("Limite de taxa de leitura excedido para usuário: {}", userId);
        throw new ResponseStatusException(TOO_MANY_REQUESTS, "Read rate exceeded");
    }

    @GetMapping("/r/{code}")
    @RateLimiter(name = "readLimiter", fallbackMethod = "readFallbackRedirect")
    public ResponseEntity<Void> resolveLink(@PathVariable String code) {
        logger.info("Resolvendo link com código: {}", code);
        
        try {
            String targetUrl = applicationService.resolveLink(code);
            logger.info("Link resolvido com sucesso: código={}, urlDestino={}", code, targetUrl);
            return ResponseEntity.status(FOUND).location(URI.create(targetUrl)).build();
        } catch (Exception e) {
            logger.error("Falha ao resolver link com código: {}", code, e);
            throw e;
        }
    }
    
    public ResponseEntity<Void> readFallbackRedirect(String code, RequestNotPermitted requestNotPermitted) {
        logger.warn("Limite de taxa de leitura excedido para resolução de link: código={}", code);
        return ResponseEntity.status(TOO_MANY_REQUESTS).build();
    }

    @DeleteMapping("/links/{code}")
    @RateLimiter(name = "writeLimiter", fallbackMethod = "writeFallbackDelete")
    public ResponseEntity<Void> deleteLink(@PathVariable String code, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        logger.info("Deletando link: código={}, usuário={}", code, userId);
        
        try {
            applicationService.deleteLink(code, userId);
            logger.info("Link deletado com sucesso: código={}, usuário={}", code, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Falha ao deletar link: código={}, usuário={}", code, userId, e);
            throw e;
        }
    }
    
    public ResponseEntity<Void> writeFallbackDelete(String code, String userId, RequestNotPermitted requestNotPermitted) {
        logger.warn("Limite de taxa de escrita excedido para deleção de link: código={}, usuário={}", code, userId);
        return ResponseEntity.status(TOO_MANY_REQUESTS).build();
    }
}
