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

## Configuração

### 1. Banco de Dados
Crie um banco PostgreSQL e configure as variáveis no arquivo `.env`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auth_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

### 2. JWT Secret
Configure o secret para assinatura dos tokens JWT:

```env
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=3600000
```

### 3. Executar o Projeto

```bash
# Clone o repositório
git clone https://github.com/vBaggio/auth.git
cd auth

# Copie o arquivo de exemplo
cp env.example .env

# Configure as variáveis no .env

# Execute o projeto
mvn spring-boot:run
```

### 4. Swagger (OpenAPI)

- UI: acesse `http://localhost:8080/swagger-ui/index.html`
- Documentos: `http://localhost:8080/v3/api-docs` (JSON)
- As rotas do Swagger estão liberadas no `SecurityConfig`.
- Para testar endpoints protegidos via Swagger UI: clique em "Authorize" e informe `Bearer <seu_token>`.

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

## Usuário Admin Padrão

O sistema cria automaticamente um usuário admin padrão (via Flyway):

- **Email:** admin@admin.com
- **Senha:** segredo123
- **Role:** ADMIN

## Segurança

- Tokens JWT com validade de 60 minutos
- Senhas criptografadas com BCrypt
- CORS configurado para localhost
- Validação de entrada com Bean Validation
- Controle de acesso baseado em roles
- **Tratamento seguro de erros** - Não exposição de informações sensíveis
- **Global Exception Handler** - Tratamento centralizado de exceções
- **Exceções customizadas** - Mensagens de erro padronizadas e seguras
- **Logs seguros** - Informações sensíveis não são logadas

## Swagger/OpenAPI

- Dependência: `org.springdoc:springdoc-openapi-starter-webmvc-ui`
- Configurações úteis em `application.properties`:

```properties
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.display-operation-id=true
```

- Configuração adicional em `com.auth.config.SwaggerConfig` para metadados e esquema de segurança Bearer.
- Endpoints liberados: `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`, `/api-docs/**`.

Veja mais detalhes em `SWAGGER.md`.

## MapStruct

- Usado para mapear entidades ↔ DTOs.
- Mappers: `AuthMapper`, `UserMapper`, `RoleMapper` em `com.auth.mapper`.
- Implementações geradas em tempo de compilação (ex.: `UserMapperImpl` em `target/generated-sources`).
- Processador configurado no `maven-compiler-plugin`.

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

## Desenvolvimento

Para contribuir com o projeto:

1. Faça um fork do repositório
2. Crie uma branch para sua feature
3. Faça commit das mudanças
4. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT.

## Recursos Úteis

- Documentação da API: `http://localhost:8080/swagger-ui/index.html`
- Coleções Postman: `Auth-Service-Collection.postman_collection.json` e `Auth-Service-Environment.postman_environment.json`
- Docker/PostgreSQL: veja `DOCKER.md`
- Migrações de banco: veja `FLYWAY.md`
