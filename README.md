# Auth Service - Spring Boot 3.5.6

Sistema de autenticação e autorização robusto utilizando Spring Boot 3.5.6, Java 21, Spring Security 6, OAuth2 e JWT.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Security 6**
- **OAuth2 Resource Server**
- **JWT (JSON Web Tokens)**
- **PostgreSQL**
- **Spring Data JPA**
- **Flyway** (migrações de banco)
- **MapStruct** (mapeamento de DTOs)
- **Swagger/OpenAPI (springdoc)**
- **Maven**

## Funcionalidades

### Authentication Service
- Registro de usuários
- Login com geração de JWT
- Validação de tokens JWT
- Criptografia de senhas com BCrypt

### Authorization Service (Resource Server)
- Endpoints protegidos por JWT
- Controle de acesso baseado em roles
- CORS configurado para localhost

### Roles
- **ADMIN**: Acesso total ao sistema
- **DEFAULT**: Usuário padrão (apenas acesso aos próprios dados)

## Início Rápido

### 1. Pré-requisitos
- Java 21+
- Maven 3.6+
- Docker e Docker Compose (opcional)

### 2. Configuração do Banco de Dados

#### Opção A: Docker (Recomendado)
```bash
# Subir o banco PostgreSQL
docker-compose up -d postgres

# Verificar se está funcionando
docker-compose logs -f postgres
```

**Personalização das Credenciais do Banco:**
Para usar credenciais diferentes, edite o `docker-compose.yml`:
```yaml
environment:
  POSTGRES_DB: seu_banco
  POSTGRES_USER: seu_usuario
  POSTGRES_PASSWORD: sua_senha
```

E ajuste o `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

#### Opção B: PostgreSQL Local
Crie um banco PostgreSQL e configure as credenciais no `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 3. Configuração do Projeto

#### Configuração Principal
O projeto usa `application.properties` como configuração principal. Os valores padrão são:

```properties
# Database Configuration (valores padrão)
spring.datasource.url=jdbc:postgresql://localhost:5432/auth
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT Configuration (valores padrão)
app.jwt.secret=123456789012345678901234567890123456789012345678901234567890
app.jwt.expiration=3600000
```

#### Personalização via Variáveis de Ambiente
Para sobrescrever os valores padrão, use variáveis de ambiente do sistema:

```bash
# Windows (PowerShell)
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="auth"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET_KEY="mySecretKey123456789012345678901234567890"
$env:JWT_EXPIRATION="3600000"
$env:SERVER_PORT="8080"

# Linux/Mac
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=auth
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET_KEY=mySecretKey123456789012345678901234567890
export JWT_EXPIRATION=3600000
export SERVER_PORT=8080
```

**Nota:** Se as variáveis de ambiente não forem definidas, o projeto usará os valores padrão do `application.properties`.

### 4. Executar o Projeto

```bash
# Clone o repositório
git clone https://github.com/vBaggio/auth.git
cd auth

# Execute o projeto
mvn spring-boot:run
```

### 5. Verificar Funcionamento

- **API:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Health Check:** `http://localhost:8080/actuator/health`

## Endpoints da API

### Autenticação (Públicos)

#### POST /api/auth/register
Registra um novo usuário.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "João",
  "lastName": "Silva"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "user@example.com",
  "firstName": "João",
  "lastName": "Silva",
  "expiresAt": "2024-01-01T12:00:00"
}
```

#### POST /api/auth/login
Realiza login e retorna JWT.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "user@example.com",
  "firstName": "João",
  "lastName": "Silva",
  "expiresAt": "2024-01-01T12:00:00"
}
```

### Usuários (Protegidos)

#### GET /api/users/me
Retorna dados do usuário logado.

**Headers:**
```
Authorization: Bearer <token>
```

#### GET /api/users
Lista todos os usuários (apenas ADMIN).

**Headers:**
```
Authorization: Bearer <admin_token>
```

#### GET /api/users/{id}
Retorna dados de um usuário específico (apenas ADMIN).

**Headers:**
```
Authorization: Bearer <admin_token>
```

#### GET /api/users/email/{email}
Retorna dados de um usuário por email (apenas ADMIN).

**Headers:**
```
Authorization: Bearer <admin_token>
```

## Usuário Admin Padrão

O sistema cria automaticamente um usuário admin padrão (via Flyway):

- **Email:** admin@admin.com
- **Senha:** segredo123
- **Role:** ADMIN

## Tratamento de Erros

O sistema implementa um tratamento seguro de erros que evita a exposição de informações sensíveis:

### Resposta Padrão de Erro
```json
{
  "message": "Mensagem amigável em português",
  "error": "CODIGO_ERRO_ESPECIFICO",
  "status": 400,
  "timestamp": "2024-01-01T12:00:00",
  "path": "/api/auth/register"
}
```

### Códigos de Erro
- `EMAIL_ALREADY_EXISTS` - Email já está em uso
- `INVALID_CREDENTIALS` - Credenciais inválidas
- `USER_NOT_FOUND` - Usuário não encontrado
- `ROLE_NOT_FOUND` - Função não encontrada
- `VALIDATION_ERROR` - Dados de entrada inválidos
- `ACCESS_DENIED` - Acesso negado
- `AUTHENTICATION_FAILED` - Falha na autenticação
- `INTERNAL_ERROR` - Erro interno do servidor

### Exemplos de Respostas de Erro

#### Email já em uso (409)
```json
{
  "message": "Este email já está em uso",
  "error": "EMAIL_ALREADY_EXISTS",
  "status": 409,
  "timestamp": "2024-01-01T12:00:00",
  "path": "/api/auth/register"
}
```

#### Credenciais inválidas (401)
```json
{
  "message": "Credenciais inválidas",
  "error": "INVALID_CREDENTIALS",
  "status": 401,
  "timestamp": "2024-01-01T12:00:00",
  "path": "/api/auth/login"
}
```

#### Dados de validação inválidos (400)
```json
{
  "message": "Dados de entrada inválidos",
  "error": "VALIDATION_ERROR",
  "status": 400,
  "timestamp": "2024-01-01T12:00:00",
  "path": "/api/auth/register"
}
```

## Documentação da API

### Swagger/OpenAPI

O projeto utiliza `springdoc-openapi` para documentação automática da API:

#### Acesso
- **UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

#### Autenticação
- Clique em "Authorize" na UI do Swagger
- Informe: `Bearer <seu_token_jwt>`
- O esquema de segurança Bearer (JWT) é configurado em `SwaggerConfig`

## Migrações de Banco

### Flyway

O projeto utiliza Flyway para versionamento e migração do banco de dados:

#### Configuração
- **Localização dos scripts:** `src/main/resources/db/migration/`
- **Convenção de nomenclatura:** `V{versão}__{descrição}.sql`
- **Scripts disponíveis:**
  - `V1__Create_initial_tables.sql` - Criação das tabelas iniciais
  - `V2__Insert_initial_data.sql` - Inserção dos dados iniciais

#### Comandos Úteis
```bash
# Verificar status das migrações
mvn flyway:info

# Executar migrações manualmente
mvn flyway:migrate

# Validar migrações
mvn flyway:validate
```

## Segurança

- **Tokens JWT** com validade de 60 minutos (configurável)
- **Senhas criptografadas** com BCrypt (cost 10)
- **CORS configurado** para localhost:3000 e localhost:8080
- **Validação de entrada** com Bean Validation
- **Controle de acesso** baseado em roles
- **Tratamento seguro de erros** - Não exposição de informações sensíveis
- **Global Exception Handler** - Tratamento centralizado de exceções
- **Exceções customizadas** - Mensagens de erro padronizadas e seguras
- **Logs seguros** - Informações sensíveis não são logadas

## Mapeamento de Dados

### MapStruct

- **Usado para mapear** entidades ↔ DTOs
- **Mappers:** `AuthMapper`, `UserMapper`, `RoleMapper` em `com.auth.mapper`
- **Implementações geradas** em tempo de compilação (ex.: `UserMapperImpl` em `target/generated-sources`)
- **Processador configurado** no `maven-compiler-plugin`

## Estrutura do Projeto

```
src/main/java/com/auth/
├── AuthServiceApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── dto/
│   ├── AuthDTO.java
│   ├── ErrorDTO.java
│   ├── LoginDTO.java
│   ├── RegisterDTO.java
│   ├── RoleDTO.java
│   └── UserDTO.java
├── entity/
│   ├── Role.java
│   └── User.java
├── exception/
│   ├── AuthException.java
│   ├── EmailAlreadyExistsException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCredentialsException.java
│   ├── RoleNotFoundException.java
│   └── UserNotFoundException.java
├── mapper/
│   ├── AuthMapper.java
│   ├── RoleMapper.java
│   └── UserMapper.java
├── repository/
│   ├── RoleRepository.java
│   └── UserRepository.java
├── security/
│   └── JwtAuthenticationFilter.java
└── service/
    ├── AuthService.java
    ├── JwtService.java
    └── UserService.java
```

## Docker

### Comandos Úteis

```bash
# Subir o banco de dados
docker-compose up -d postgres

# Ver logs
docker-compose logs -f postgres

# Parar o banco
docker-compose down

# Resetar banco (remove dados)
docker-compose down -v
```

### Configurações do Container
- **Imagem:** postgres:15-alpine
- **Porta:** 5432
- **Volume:** postgres_data (dados persistentes)
- **Health Check:** Configurado para verificar disponibilidade

## Desenvolvimento

Para contribuir com o projeto:

1. Faça um fork do repositório
2. Crie uma branch para sua feature
3. Faça commit das mudanças
4. Abra um Pull Request

## Recursos Úteis

- **Documentação da API:** `http://localhost:8080/swagger-ui/index.html`
- **Coleções Postman:** `Auth-Service-Collection.postman_collection.json` e `Auth-Service-Environment.postman_environment.json`

## Licença

Este projeto está sob a licença MIT.