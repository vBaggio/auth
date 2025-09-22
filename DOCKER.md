# Configuração Docker para PostgreSQL

Este projeto inclui configuração Docker para rodar um banco de dados PostgreSQL compatível com as credenciais definidas no `application.properties`.

## Configurações do Banco

- **Host**: localhost
- **Porta**: 5432
- **Database**: auth
- **Usuário**: postgres
- **Senha**: postgres

## Como usar

### Opção 1: Usando Docker Compose (Recomendado)

```bash
# Subir o banco de dados
docker-compose up -d postgres

# Ver logs
docker-compose logs -f postgres

# Parar o banco
docker-compose down
```

### Opção 2: Usando o script PowerShell

```powershell
# Executar o script interativo
.\docker-scripts.ps1
```

### Opção 3: Comandos Docker diretos

```bash
# Subir apenas o PostgreSQL
docker-compose up -d postgres

# Verificar status
docker-compose ps

# Conectar ao banco via psql
docker-compose exec postgres psql -U postgres -d auth

# Parar e remover containers
docker-compose down

# Parar, remover containers e volumes (CUIDADO: apaga dados)
docker-compose down -v
```

## Verificação

Após subir o container, você pode verificar se está funcionando:

1. **Via aplicação Spring**: Execute sua aplicação e ela deve conectar automaticamente
2. **Via psql**: Use o comando de conexão acima
3. **Via logs**: `docker-compose logs postgres`

## Dados Persistentes

Os dados do PostgreSQL são armazenados em um volume Docker chamado `postgres_data`, então os dados persistem mesmo se você parar e subir o container novamente.

## Troubleshooting

### Porta já em uso
Se a porta 5432 já estiver em uso, você pode alterar no `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"  # Mude para 5433
```
E ajustar no `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/auth
```

### Container não inicia
```bash
# Ver logs detalhados
docker-compose logs postgres

# Verificar se a porta está livre
netstat -an | findstr :5432
```

### Resetar banco de dados
```bash
# Parar e remover dados
docker-compose down -v

# Subir novamente
docker-compose up -d postgres
```
