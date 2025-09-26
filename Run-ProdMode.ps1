# Run-ProdMode.ps1
# Script para executar a aplicação no modo produção

Write-Host "Iniciando Auth Service em modo PRODUÇÃO..." -ForegroundColor Yellow
$env:SPRING_PROFILES_ACTIVE="prod"
mvn spring-boot:run