package br.com.dev.tiny.service.urlshortener.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UrlUtils {

    public static String buildBaseUrl(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        String host = request.getHeader("X-Forwarded-Host");
        
        if (scheme != null && host != null) {
            return scheme + "://" + host;
        }
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
