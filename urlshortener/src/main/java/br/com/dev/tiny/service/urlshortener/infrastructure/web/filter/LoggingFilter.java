package br.com.dev.tiny.service.urlshortener.infrastructure.web.filter;

import br.com.dev.tiny.service.urlshortener.infrastructure.config.LoggingUtils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
@Order(1)
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = LoggingUtils.generateCorrelationId();
            }
            LoggingUtils.setCorrelationId(correlationId);

            String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }
            LoggingUtils.setRequestId(requestId);

            String userId = httpRequest.getHeader("X-User-Id");
            if (userId != null && !userId.isEmpty()) {
                LoggingUtils.setUserId(userId);
            }

            String operation = extractOperation(httpRequest);
            LoggingUtils.setOperation(operation);

            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
            httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

            logger.info("Requisição recebida: {} {} de {}", 
                httpRequest.getMethod(), 
                httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr());

            long startTime = System.currentTimeMillis();

            chain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Requisição concluída: {} {} - Status: {} - Duração: {}ms", httpRequest.getMethod(), httpRequest.getRequestURI(), httpResponse.getStatus(), duration);
        } finally {
            LoggingUtils.clearMDC();
        }
    }

    private String extractOperation(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        if (uri.contains("/api/v1/links") && "POST".equals(method)) {
            return "CREATE_LINK";
        } else if (uri.contains("/api/v1/links") && "GET".equals(method)) {
            return "LIST_LINKS";
        } else if (uri.contains("/api/v1/r/") && "GET".equals(method)) {
            return "RESOLVE_LINK";
        } else if (uri.contains("/api/v1/links/") && "DELETE".equals(method)) {
            return "DELETE_LINK";
        }
        
        return method + "_" + uri.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
