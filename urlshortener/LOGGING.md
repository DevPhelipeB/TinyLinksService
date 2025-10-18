# Sistema de Logging - TinyLinks Service

## Visão Geral

O TinyLinks Service implementa um sistema de logging seguindo as melhores práticas de observabilidade e tracing distribuído para ajudar no MTTR.

## Características

### **Logging Estruturado**
- Logs em formato JSON para facilitar parsing e análise
- Uso de SLF4J + Logback para máxima flexibilidade
- Logs estruturados com campos padronizados

### **Correlation ID & Tracing**
- Correlation ID automático para rastrear requisições
- MDC (Mapped Diagnostic Context) para contexto compartilhado
- Integração com Micrometer Tracing para observabilidade

### **Níveis de Log**
- **ERROR**: Erros críticos que requerem atenção imediata
- **WARN**: Situações que podem indicar problemas
- **INFO**: Informações importantes sobre operações
- **DEBUG**: Detalhes técnicos para desenvolvimento

## Configuração

### Logback Configuration
O arquivo `logback-spring.xml` configura:
- Console appender para desenvolvimento
- File appender com rotação para produção
- Logs estruturados em JSON
- Configurações específicas por profile

### Application Properties
```yaml
logging:
  level:
    br.com.dev.tiny.service.urlshortener: INFO
    org.springframework.web: INFO
    org.springframework.data.mongodb: INFO
    io.github.resilience4j: INFO
```

## Estrutura dos Logs

### Campos Padronizados
```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "level": "INFO",
  "logger": "br.com.dev.tiny.service.urlshortener.LinkController",
  "message": "Link criado com sucesso: código=abc123, urlCurta=http://localhost:8080/r/abc123, usuário=user123",
  "correlationId": "abc12345",
  "userId": "user123",
  "operation": "CREATE_LINK",
  "service": "tinylinks-service",
  "version": "0.1.0",
  "environment": "prod"
}
```

### MDC Context
- `correlationId`: ID único para rastrear requisições
- `userId`: ID do usuário (quando disponível)
- `requestId`: ID único da requisição
- `operation`: Tipo de operação sendo executada

## Tracing Distribuído

### Micrometer Integration
- Integração com Zipkin para tracing distribuído
- Spans automáticos para operações HTTP
- Métricas de performance

### Correlation ID Flow
1. **Request**: Correlation ID é extraído do header ou gerado
2. **MDC**: ID é adicionado ao contexto de thread
3. **Logs**: Todos os logs incluem o correlation ID
4. **Response**: ID é retornado no header da resposta

## Monitoramento

### Logs de Produção
- Rotação automática de arquivos
- Compressão de logs antigos
- Retenção configurável (30 dias)
- Limite de tamanho por arquivo (100MB)

### Métricas Disponíveis
- Número de requisições por endpoint
- Tempo de resposta
- Taxa de erro
- Uso de quota por usuário


### Debug Mode
Para ativar logs de debug em desenvolvimento:
```yaml
logging:
  level:
    br.com.dev.tiny.service.urlshortener: DEBUG
```

