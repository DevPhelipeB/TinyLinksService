package br.com.dev.tiny.service.urlshortener.infrastructure.web.controller;

import br.com.dev.tiny.service.urlshortener.application.dto.CreateLinkRequest;
import br.com.dev.tiny.service.urlshortener.application.dto.LinkResponse;
import br.com.dev.tiny.service.urlshortener.application.service.LinkApplicationService;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    
    private final LinkApplicationService applicationService;
    
    public LinkController(LinkApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    /** TODO - Criar classe do utilitário
     * Utilitário para construir URL base dos links encurtados.
     */
    private static String buildBaseUrl(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        String host = request.getHeader("X-Forwarded-Host");
        if (scheme != null && host != null) {
            return scheme + "://" + host;
        }
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    @PostMapping("/links")
    @RateLimiter(name = "writeLimiter", fallbackMethod = "writeFallback")
    public LinkResponse createLink(@Valid @RequestBody CreateLinkRequest request, @RequestHeader(value = "X-User-Id", required = false) String userId, HttpServletRequest httpRequest) {
        return applicationService.createLink(request, userId, buildBaseUrl(httpRequest));
    }
    
    public LinkResponse writeFallback(CreateLinkRequest request, String userId, HttpServletRequest httpRequest, RequestNotPermitted requestNotPermitted) {
        throw new ResponseStatusException(TOO_MANY_REQUESTS, "Write rate exceeded");
    }

    @GetMapping("/links")
    @RateLimiter(name = "readLimiter", fallbackMethod = "readFallbackPage")
    public Page<LinkResponse> listUserLinks(@RequestHeader(value = "X-User-Id", required = false) String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest httpRequest) {
        return applicationService.listUserLinks(userId, PageRequest.of(page, size), buildBaseUrl(httpRequest));
    }
    
    public Page<LinkResponse> readFallbackPage(String userId, int page, int size, HttpServletRequest httpRequest, RequestNotPermitted requestNotPermitted) {
        throw new ResponseStatusException(TOO_MANY_REQUESTS, "Read rate exceeded");
    }

    @GetMapping("/r/{code}")
    @RateLimiter(name = "readLimiter", fallbackMethod = "readFallbackRedirect")
    public ResponseEntity<Void> resolveLink(@PathVariable String code) {
        String targetUrl = applicationService.resolveLink(code);
        return ResponseEntity.status(FOUND).location(URI.create(targetUrl)).build();
    }
    
    public ResponseEntity<Void> readFallbackRedirect(String code, RequestNotPermitted requestNotPermitted) {
        return ResponseEntity.status(TOO_MANY_REQUESTS).build();
    }

    @DeleteMapping("/links/{code}")
    @RateLimiter(name = "writeLimiter", fallbackMethod = "writeFallbackDelete")
    public ResponseEntity<Void> deleteLink(@PathVariable String code, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        applicationService.deleteLink(code, userId);
        return ResponseEntity.noContent().build();
    }
    
    public ResponseEntity<Void> writeFallbackDelete(String code, String userId, RequestNotPermitted requestNotPermitted) {
        return ResponseEntity.status(TOO_MANY_REQUESTS).build();
    }
}
