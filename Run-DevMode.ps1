# Run-DevMode.ps1
# Script para executar a aplicação no modo desenvolvimento

Write-Host "Iniciando Auth Service em modo DESENVOLVIMENTO..." -ForegroundColor Green
$env:SPRING_PROFILES_ACTIVE="dev,swagger"
mvn spring-boot:run