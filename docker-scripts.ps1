# Script PowerShell para gerenciar o container PostgreSQL

# Função para subir o banco de dados
function Start-Database {
    Write-Host "Iniciando container PostgreSQL..." -ForegroundColor Green
    docker-compose up -d postgres
    
    Write-Host "Aguardando o banco de dados ficar pronto..." -ForegroundColor Yellow
    do {
        $health = docker-compose ps postgres
        Start-Sleep -Seconds 2
    } while ($health -notmatch "healthy")
    
    Write-Host "Banco de dados PostgreSQL está rodando!" -ForegroundColor Green
    Write-Host "Host: localhost" -ForegroundColor Cyan
    Write-Host "Porta: 5432" -ForegroundColor Cyan
    Write-Host "Database: auth" -ForegroundColor Cyan
    Write-Host "Usuário: postgres" -ForegroundColor Cyan
    Write-Host "Senha: postgres" -ForegroundColor Cyan
}

# Função para parar o banco de dados
function Stop-Database {
    Write-Host "Parando container PostgreSQL..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "Container PostgreSQL parado!" -ForegroundColor Green
}

# Função para ver logs do banco
function Show-DatabaseLogs {
    Write-Host "Mostrando logs do PostgreSQL..." -ForegroundColor Yellow
    docker-compose logs -f postgres
}

# Função para conectar ao banco via psql
function Connect-Database {
    Write-Host "Conectando ao banco de dados..." -ForegroundColor Green
    docker-compose exec postgres psql -U postgres -d auth
}

# Função para mostrar status
function Show-Status {
    Write-Host "Status dos containers:" -ForegroundColor Cyan
    docker-compose ps
}

# Menu principal
function Show-Menu {
    Write-Host "`n=== Gerenciador PostgreSQL Docker ===" -ForegroundColor Magenta
    Write-Host "1. Iniciar banco de dados" -ForegroundColor White
    Write-Host "2. Parar banco de dados" -ForegroundColor White
    Write-Host "3. Ver logs" -ForegroundColor White
    Write-Host "4. Conectar ao banco (psql)" -ForegroundColor White
    Write-Host "5. Ver status" -ForegroundColor White
    Write-Host "6. Sair" -ForegroundColor White
    Write-Host "`nEscolha uma opção (1-6): " -NoNewline -ForegroundColor Yellow
}

# Loop principal
do {
    Show-Menu
    $choice = Read-Host
    
    switch ($choice) {
        "1" { Start-Database }
        "2" { Stop-Database }
        "3" { Show-DatabaseLogs }
        "4" { Connect-Database }
        "5" { Show-Status }
        "6" { 
            Write-Host "Saindo..." -ForegroundColor Green
            break 
        }
        default { 
            Write-Host "Opção inválida! Tente novamente." -ForegroundColor Red 
        }
    }
    
    if ($choice -ne "6") {
        Write-Host "`nPressione qualquer tecla para continuar..." -ForegroundColor Gray
        $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    }
} while ($choice -ne "6")
