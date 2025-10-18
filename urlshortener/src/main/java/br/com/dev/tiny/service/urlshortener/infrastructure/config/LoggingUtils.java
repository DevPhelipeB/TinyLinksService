package br.com.dev.tiny.service.urlshortener.infrastructure.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggingUtils {

    public static final String CORRELATION_ID_KEY = "correlationId";

    public static final String USER_ID_KEY = "userId";

    public static final String REQUEST_ID_KEY = "requestId";

    public static final String OPERATION_KEY = "operation";

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.isEmpty()) {
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
    }

    public static void setUserId(String userId) {
        if (userId != null && !userId.isEmpty()) {
            MDC.put(USER_ID_KEY, userId);
        }
    }

    public static void setRequestId(String requestId) {
        if (requestId != null && !requestId.isEmpty()) {
            MDC.put(REQUEST_ID_KEY, requestId);
        }
    }

    public static void setOperation(String operation) {
        if (operation != null && !operation.isEmpty()) {
            MDC.put(OPERATION_KEY, operation);
        }
    }

    public static void clearMDC() {
        MDC.clear();
    }

    public static void removeFromMDC(String key) {
        MDC.remove(key);
    }

    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    public static String getUserId() {
        return MDC.get(USER_ID_KEY);
    }

    public static String getRequestId() {
        return MDC.get(REQUEST_ID_KEY);
    }

    public static String getOperation() {
        return MDC.get(OPERATION_KEY);
    }
}
