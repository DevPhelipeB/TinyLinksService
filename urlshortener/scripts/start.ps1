# Script PowerShell para iniciar o TinyLinks Service com Podman no Windows
# Uso: .\scripts\start.ps1 [dev|prod|stop|logs|status|clean]

param(
    [Parameter(Position=0)]
    [ValidateSet("dev", "prod", "stop", "logs", "status", "clean")]
    [string]$Mode = "dev"
)

# Configurações
$ErrorActionPreference = "Stop"

# Função para imprimir mensagens
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARN] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Write-Header {
    Write-Host "================================" -ForegroundColor Blue
    Write-Host "  TinyLinks Service - Podman    " -ForegroundColor Blue
    Write-Host "================================" -ForegroundColor Blue
}

# Verificar se Podman está instalado
function Test-Podman {
    try {
        $podmanVersion = podman --version 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Info "Podman encontrado: $podmanVersion"
            return $true
        }
    }
    catch {
        Write-Error "Podman não está instalado. Por favor, instale o Podman primeiro."
        Write-Host "Download: https://podman-desktop.io/" -ForegroundColor Cyan
        return $false
    }
    return $false
}

# Verificar se docker-compose está disponível
function Get-ComposeCommand {
    $composeCmd = $null
    
    # Tentar podman-compose primeiro
    try {
        $null = podman-compose --version 2>$null
        if ($LASTEXITCODE -eq 0) {
            $composeCmd = "podman-compose"
        }
    }
    catch {
        # Tentar docker-compose como fallback
        try {
            $null = docker-compose --version 2>$null
            if ($LASTEXITCODE -eq 0) {
                $composeCmd = "docker-compose"
            }
        }
        catch {
            Write-Warning "Nem podman-compose nem docker-compose foram encontrados."
            Write-Info "Instalando podman-compose via pip..."
            try {
                pip install podman-compose
                $composeCmd = "podman-compose"
            }
            catch {
                Write-Error "Falha ao instalar podman-compose. Instale manualmente."
                exit 1
            }
        }
    }
    
    Write-Info "Usando comando: $composeCmd"
    return $composeCmd
}

# Função para limpar containers e volumes antigos
function Clear-Containers {
    Write-Info "Limpando containers e volumes antigos..."
    try {
        & $composeCmd down -v --remove-orphans 2>$null
    }
    catch {
        # Ignorar erros de limpeza
    }
    
    try {
        podman system prune -f 2>$null
    }
    catch {
        # Ignorar erros de limpeza
    }
}

# Função para construir a aplicação
function Build-App {
    Write-Info "Construindo a aplicação..."
    & $composeCmd build --no-cache tinylinks-app
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao construir a aplicação"
    }
}

# Função para iniciar em modo desenvolvimento
function Start-Development {
    Write-Info "Iniciando em modo DESENVOLVIMENTO..."
    
    # Iniciar MongoDB e Zipkin primeiro
    & $composeCmd --profile dev up -d mongodb zipkin
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao iniciar MongoDB e Zipkin"
    }
    
    Write-Info "Aguardando MongoDB inicializar..."
    Start-Sleep -Seconds 15
    
    # Iniciar aplicação
    & $composeCmd --profile dev up -d tinylinks-app
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao iniciar a aplicação"
    }
    
    Write-Info "Aguardando aplicação inicializar..."
    Start-Sleep -Seconds 30
    
    # Verificar saúde dos serviços
    Test-ServicesHealth
}

# Função para iniciar em modo produção
function Start-Production {
    Write-Info "Iniciando em modo PRODUÇÃO..."
    
    & $composeCmd --profile production up -d
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao iniciar serviços em modo produção"
    }
    
    Write-Info "Aguardando aplicação inicializar..."
    Start-Sleep -Seconds 60
    
    # Verificar saúde dos serviços
    Test-ServicesHealth
}

# Função para verificar saúde dos serviços
function Test-ServicesHealth {
    Write-Info "Verificando saúde dos serviços..."
    
    # Verificar MongoDB
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:27017" -TimeoutSec 5 -ErrorAction SilentlyContinue
        Write-Info "✓ MongoDB está rodando"
    }
    catch {
        Write-Warning "✗ MongoDB não está acessível"
    }
    
    # Verificar aplicação
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/tinyapp/actuator/health" -TimeoutSec 5 -ErrorAction SilentlyContinue
        Write-Info "✓ TinyLinks App está rodando"
    }
    catch {
        Write-Warning "✗ TinyLinks App não está acessível"
    }
    
    # Verificar Zipkin
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:9411" -TimeoutSec 5 -ErrorAction SilentlyContinue
        Write-Info "✓ Zipkin está rodando"
    }
    catch {
        Write-Warning "✗ Zipkin não está acessível"
    }
}

# Função para mostrar logs
function Show-Logs {
    Write-Info "Mostrando logs dos serviços..."
    & $composeCmd logs -f
}

# Função para parar serviços
function Stop-Services {
    Write-Info "Parando serviços..."
    & $composeCmd down
}

# Função para mostrar status
function Show-Status {
    Write-Info "Status dos containers:"
    podman ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

# Função para mostrar URLs úteis
function Show-Urls {
    Write-Host "================================" -ForegroundColor Blue
    Write-Host "  URLs Úteis                    " -ForegroundColor Blue
    Write-Host "================================" -ForegroundColor Blue
    Write-Host "🌐 Aplicação:     " -NoNewline; Write-Host "http://localhost:8080/tinyapp" -ForegroundColor Green
    Write-Host "📊 Health Check:  " -NoNewline; Write-Host "http://localhost:8080/tinyapp/actuator/health" -ForegroundColor Green
    Write-Host "🔍 Zipkin:        " -NoNewline; Write-Host "http://localhost:9411" -ForegroundColor Green
    Write-Host "🗄️  MongoDB:      " -NoNewline; Write-Host "mongodb://localhost:27017" -ForegroundColor Green
    Write-Host "📚 API Docs:      " -NoNewline; Write-Host "http://localhost:8080/tinyapp/swagger-ui.html" -ForegroundColor Green
    Write-Host "================================" -ForegroundColor Blue
}

# Função para mostrar ajuda
function Show-Help {
    Write-Host "Uso: .\scripts\start.ps1 [dev|prod|stop|logs|status|clean]" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Comandos:" -ForegroundColor Yellow
    Write-Host "  dev     - Iniciar em modo desenvolvimento" -ForegroundColor White
    Write-Host "  prod    - Iniciar em modo produção" -ForegroundColor White
    Write-Host "  stop    - Parar todos os serviços" -ForegroundColor White
    Write-Host "  logs    - Mostrar logs dos serviços" -ForegroundColor White
    Write-Host "  status  - Mostrar status dos containers" -ForegroundColor White
    Write-Host "  clean   - Limpar containers e volumes" -ForegroundColor White
    Write-Host ""
    Write-Host "Exemplos:" -ForegroundColor Yellow
    Write-Host "  .\scripts\start.ps1 dev" -ForegroundColor White
    Write-Host "  .\scripts\start.ps1 prod" -ForegroundColor White
    Write-Host "  .\scripts\start.ps1 stop" -ForegroundColor White
}

# Função principal
function Main {
    Write-Header
    
    # Verificar dependências
    if (-not (Test-Podman)) {
        exit 1
    }
    
    $composeCmd = Get-ComposeCommand
    if (-not $composeCmd) {
        exit 1
    }
    
    # Processar modo
    switch ($Mode) {
        "dev" {
            try {
                Clear-Containers
                Build-App
                Start-Development
                Show-Urls
            }
            catch {
                Write-Error "Erro ao iniciar em modo desenvolvimento: $($_.Exception.Message)"
                exit 1
            }
        }
        "prod" {
            try {
                Clear-Containers
                Build-App
                Start-Production
                Show-Urls
            }
            catch {
                Write-Error "Erro ao iniciar em modo produção: $($_.Exception.Message)"
                exit 1
            }
        }
        "stop" {
            Stop-Services
        }
        "logs" {
            Show-Logs
        }
        "status" {
            Show-Status
        }
        "clean" {
            Clear-Containers
        }
        default {
            Show-Help
        }
    }
}

# Executar função principal
try {
    Main
}
catch {
    Write-Error "Erro inesperado: $($_.Exception.Message)"
    exit 1
}
