# TinyLinks Service - Docker/Podman Setup

Documento visa ajudar como executar o TinyLinks Service usando Docker ou Podman no Windows para teste local.

## Pré-requisitos

### Windows
- **Podman Desktop**
- **PowerShell**

## Início Rápido

### 1. Clonar o Repositório
```bash
git clone <repository-url>
cd urlshortener
```

### 2. Executar com PowerShell (Recomendado)
```powershell
# Modo desenvolvimento
.\scripts\start.ps1 dev

# Modo produção
.\scripts\start.ps1 prod

# Parar serviços
.\scripts\start.ps1 stop

# Ver logs
.\scripts\start.ps1 logs

# Ver status
.\scripts\start.ps1 status
```

## Arquitetura dos Containers

### Serviços Incluídos

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| **tinylinks-app** | 8080 | Aplicação Spring Boot |
| **mongodb** | 27017 | Banco de dados MongoDB |
| **zipkin** | 9411 | Distributed Tracing |
| **nginx** | 80/443 | Reverse Proxy |

### Volumes Persistentes
- `mongodb_data`: Dados do MongoDB
- `app_logs`: Logs da aplicação

## Configurações

### Variáveis de Ambiente

#### Desenvolvimento
```yaml
SPRING_PROFILES_ACTIVE: dev,docker
TINYLINKS_QUOTA_PERUSERPERDAY: 1000
LOGGING_LEVEL_BR_COM_DEV_TINY_SERVICE_URLSHORTENER: DEBUG
```

#### Produção
```yaml
SPRING_PROFILES_ACTIVE: docker
TINYLINKS_QUOTA_PERUSERPERDAY: 200
LOGGING_LEVEL_BR_COM_DEV_TINY_SERVICE_URLSHORTENER: INFO
```

### MongoDB
- **Usuário Admin**: `admin` / `admin123`
- **Usuário App**: `tinylinks_user` / `tinylinks_password`
- **Database**: `tinylinks`

## URLs de Acesso

Após iniciar os serviços:

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **Aplicação** | http://localhost:8080/tinyapp | API principal |
| **Health Check** | http://localhost:8080/tinyapp/actuator/health | Status da aplicação |
| **API Docs** | http://localhost:8080/tinyapp/swagger-ui.html | Documentação Swagger |
| **Zipkin** | http://localhost:9411 | Distributed Tracing |
| **MongoDB** | mongodb://localhost:27017 | Banco de dados |

## Monitoramento

### Logs
```bash
# Ver logs de todos os serviços
.\scripts\start.ps1 logs

# Ver logs específicos
podman logs tinylinks-app
podman logs tinylinks-mongodb
```

### Health Checks
```bash
# Verificar saúde da aplicação
curl http://localhost:8080/tinyapp/actuator/health

# Verificar status dos containers
.\scripts\start.ps1 status
```

### Métricas
- **Actuator**: http://localhost:8080/tinyapp/actuator/metrics
- **Zipkin**: http://localhost:9411

## Troubleshooting

### Problemas Comuns

#### 1. Porta já em uso
```bash
# Verificar processos usando a porta
netstat -ano | findstr :8080

# Parar processo específico
taskkill /PID <PID> /F
```

#### 2. Podman não encontrado
- Instalar Podman Desktop: https://podman-desktop.io/
- Reiniciar o terminal após instalação

#### 3. Falha na conexão com MongoDB
```bash
# Verificar se MongoDB está rodando
podman logs tinylinks-mongodb

# Reiniciar MongoDB
podman restart tinylinks-mongodb
```

#### 4. Aplicação não inicia
```bash
# Verificar logs da aplicação
podman logs tinylinks-app

# Verificar variáveis de ambiente
podman exec tinylinks-app env
```

### Logs de Debug
```bash
# Habilitar logs detalhados
$env:LOGGING_LEVEL_BR_COM_DEV_TINY_SERVICE_URLSHORTENER="DEBUG"
.\scripts\start.ps1 dev
```

### Rebuild da Aplicação
```bash
# Reconstruir e reiniciar
.\scripts\start.ps1 clean
.\scripts\start.ps1 dev
```

### Backup do MongoDB
```bash
# Backup
podman exec tinylinks-mongodb mongodump --out /backup

# Restore
podman exec tinylinks-mongodb mongorestore /backup
```

---

Para mais informações, consulte a documentação principal do projeto.
