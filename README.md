# Auth Service - Spring Boot 3.5

Sistema de autenticação e autorização robusto utilizando Spring Boot 3.5, Java 21, Spring Security 6, OAuth2 e JWT.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5**
- **Spring Security 6**
- **OAuth2 Resource Server**
- **JWT (JSON Web Tokens)**
- **PostgreSQL**
- **Spring Data JPA**
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

O sistema cria automaticamente um usuário admin padrão:

- **Email:** admin@admin.com
- **Senha:** S&cr3T#120
- **Role:** ADMIN

## Segurança

- Tokens JWT com validade de 60 minutos
- Senhas criptografadas com BCrypt
- CORS configurado para localhost
- Validação de entrada com Bean Validation
- Controle de acesso baseado em roles

## Estrutura do Projeto

```
src/main/java/com/auth/
├── AuthServiceApplication.java
├── config/
│   ├── DataInitializer.java
│   ├── EnvConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── dto/
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── UserResponse.java
├── entity/
│   ├── Role.java
│   └── User.java
├── repository/
│   ├── RoleRepository.java
│   └── UserRepository.java
├── security/
│   └── JwtAuthenticationFilter.java
└── service/
    ├── AuthService.java
    ├── JwtService.java
    ├── UserDetailsServiceImpl.java
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
