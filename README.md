# Auth Service - Spring Boot 3.5.6

Sistema de autenticação e autorização robusto implementando **Clean Architecture** com Spring Boot 3.5.6, Java 21, Spring Security 6 e JWT. O projeto segue padrões modernos de desenvolvimento com arquitetura em camadas bem definidas.

## Tecnologias Utilizadas

### Core Framework
- **Java 21** - Linguagem de programação com recursos modernos
- **Spring Boot 3.5.6** - Framework principal
- **Spring Security 6** - Segurança e autenticação
- **Spring Data JPA** - Persistência de dados
- **Maven** - Gerenciamento de dependências

### Segurança
- **JWT (JSON Web Tokens)** - Autenticação stateless
- **BCrypt** - Criptografia de senhas
- **OAuth2 Resource Server** - Servidor de recursos

### Banco de Dados
- **PostgreSQL** - Banco de dados principal (produção)
- **H2 Database** - Banco em memória (testes)
- **Flyway** - Migrações versionadas

### Utilitários
- **MapStruct 1.6.3** - Mapeamento objeto-a-objeto
- **OpenAPI 3 (SpringDoc)** - Documentação da API
- **Docker** - Containerização

### Testes
- **JUnit 5** - Framework de testes
- **Mockito 5.17.0** - Mocking para testes unitários
- **Spring Boot Test** - Testes de integração
- **AssertJ** - Assertions fluentes
- **Spring Security Test** - Testes de segurança

## Arquitetura do Sistema

O projeto implementa uma **Arquitetura em Camadas** seguindo princípios de Clean Architecture:

### Estrutura das Camadas
```
┌─────────────────────────┐
│   Presentation Layer    │ ← Controllers, DTOs
├─────────────────────────┤
│   Application Layer     │ ← Services, Business Logic  
├─────────────────────────┤
│     Domain Layer        │ ← Entities, Domain Models
├─────────────────────────┤
│  Infrastructure Layer   │ ← Repositories, External APIs
└─────────────────────────┘
```

### Princípios Arquiteturais
- **Separação de Responsabilidades**: Cada camada tem responsabilidades específicas
- **Inversão de Dependências**: Dependências apontam para o núcleo do domínio
- **Segurança por Padrão**: Autenticação JWT stateless com autorização baseada em roles
- **Integridade dos Dados**: Gerenciamento transacional e validação em múltiplas camadas

## Funcionalidades

### Authentication Service
- **Registro de usuários** com validação de dados
- **Login com geração de JWT** e expiração configurável
- **Validação de tokens JWT** em tempo real
- **Criptografia de senhas** com BCrypt (cost 10)
- **Gerenciamento de contexto de segurança**

### Authorization Service (Resource Server)
- **Endpoints protegidos** por JWT
- **Controle de acesso baseado em roles** (@PreAuthorize)
- **CORS configurado** para desenvolvimento local
- **Filtro de autenticação personalizado**

### User Management
- **CRUD de usuários** com diferentes níveis de acesso
- **Perfis de usuário** com informações pessoais
- **Sistema de roles** flexível e extensível

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

## Testes Unitários

O projeto implementa uma estratégia abrangente de testes seguindo as melhores práticas de desenvolvimento:

### Estratégia de Testes
- **Testes Unitários**: Isolamento de componentes com mocks
- **Testes de Integração**: Contexto completo da aplicação
- **Testes de Repository**: Slice testing com @DataJpaTest
- **Cobertura de Exceções**: Cenários de erro e edge cases

### Stack de Testes
```
JUnit 5          - Framework principal de testes
Mockito 5.17.0   - Mocking e stubbing
AssertJ          - Assertions fluentes e expressivas
Spring Test      - Testes de integração
H2 Database      - Banco em memória para testes
```

### Estrutura de Testes
```
src/test/java/com/auth/
├── config/           # Configurações específicas de teste
├── controller/       # Testes de controllers
├── integration/      # Testes de integração
├── repository/       # Testes de repository
└── service/          # Testes de serviços
```

### Cobertura Atual

#### AuthService (service/AuthServiceTest.java)
✅ **Cenários Testados:**
- ✅ Registro de usuário com sucesso
- ✅ Falha no registro por email duplicado
- ✅ Falha no registro por role não encontrada
- ✅ Login com sucesso
- ✅ Falha no login por credenciais inválidas
- ✅ Obtenção do usuário atual autenticado
- ✅ Comportamento quando não há autenticação

**Detalhes Técnicos:**
- **Padrão AAA**: Arrange, Act, Assert
- **Mocks**: UserRepository, RoleRepository, PasswordEncoder, JwtService
- **Assertions**: AssertJ para verificações fluentes
- **Verificações**: Chamadas de métodos e parâmetros

#### UserRepository (repository/UserRepositoryTest.java)
✅ **Cenários Testados:**
- ✅ Verificação de existência por email (`existsByEmail`)
- ✅ Busca de usuário por email (`findByEmail`)
- ✅ Busca de usuário com roles (`findByIdWithRoles`)
- ✅ Listagem de usuários com roles (`findAllWithRoles`)

**Detalhes Técnicos:**
- **@DataJpaTest**: Slice testing focado em persistência
- **H2 Database**: Banco em memória para isolamento
- **Test Containers**: Preparação para testes com PostgreSQL
- **Flyway Disabled**: Migrações desabilitadas em testes

### Configuração de Testes

#### application-test.properties
```properties
# Banco H2 em memória
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# JPA para testes
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Flyway desabilitado
spring.flyway.enabled=false

# JWT para testes
app.jwt.secret=test-secret-key-for-jwt-test-test-test-test-test-test-test
app.jwt.expiration=3600000
```

### Executando os Testes

#### Todos os Testes
```bash
# Maven
mvn test

# Com relatórios detalhados
mvn test -Dtest.reports=true
```

#### Testes Específicos
```bash
# Apenas testes de service
mvn test -Dtest="*ServiceTest"

# Apenas testes de repository
mvn test -Dtest="*RepositoryTest"

# Teste específico
mvn test -Dtest=AuthServiceTest#deveRegistrarUsuarioComSucesso
```

#### Relatórios de Teste
```bash
# Gerar relatório de cobertura
mvn jacoco:report

# Visualizar em: target/site/jacoco/index.html
```

### Padrões de Teste Implementados

#### 1. Padrão AAA (Arrange-Act-Assert)
```java
@Test
void deveRegistrarUsuarioComSucesso() {
    // ARRANGE - Configuração dos dados e mocks
    when(userRepository.existsByEmail("email")).thenReturn(false);
    
    // ACT - Execução do método testado
    AuthDTO result = authService.register(registerDTO);
    
    // ASSERT - Verificação dos resultados
    assertThat(result.token()).isEqualTo(expectedToken);
}
```

#### 2. Mocking com Mockito
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private AuthService authService;
    
    // Configuração de comportamento
    when(mock.method()).thenReturn(value);
    
    // Verificação de chamadas
    verify(mock).method(anyString());
}
```

#### 3. Testes de Exceção
```java
@Test
void deveLancarExcecaoQuandoEmailJaExiste() {
    assertThatThrownBy(() -> authService.register(registerDTO))
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessage("Este email já está em uso");
}
```

### Próximos Passos (Roadmap de Testes)

#### 🔄 Em Desenvolvimento
- [ ] Controller Tests (AuthController, UserController)
- [ ] Integration Tests (Full application context)
- [ ] Security Tests (JWT validation, CORS)
- [ ] Custom Exception Tests

#### 🎯 Planejado
- [ ] Performance Tests (Load testing)
- [ ] Contract Tests (API contracts)
- [ ] Mutation Testing (Test quality)
- [ ] TestContainers (PostgreSQL real tests)

### Convenções de Teste

#### Nomenclatura
- **Classes**: `NomeClasseTest.java`
- **Métodos**: `deve{Acao}Quando{Condicao}()` 
- **Display Names**: Descrições em português

#### Estrutura de Métodos
```java
@Test
@DisplayName("Deve fazer algo quando condição específica")
void deveFazerAlgoQuandoCondicaoEspecifica() {
    // ARRANGE
    // ACT  
    // ASSERT
    // VERIFY (se necessário)
}
```

#### Dados de Teste
- Setup em `@BeforeEach` para reutilização
- Builders para objetos complexos
- Constantes para valores fixos
- Factories para criação de objetos

## Mapeamento de Dados

### MapStruct

- **Usado para mapear** entidades ↔ DTOs
- **Mappers:** `AuthMapper`, `UserMapper`, `RoleMapper` em `com.auth.mapper`
- **Implementações geradas** em tempo de compilação (ex.: `UserMapperImpl` em `target/generated-sources`)
- **Processador configurado** no `maven-compiler-plugin`

## Estrutura do Projeto (Clean Architecture)

O projeto segue os princípios de Clean Architecture com separação clara de responsabilidades:

```
src/
├── main/java/com/auth/
│   ├── AuthServiceApplication.java         # Application Entry Point
│   │
│   ├── config/                            # 🔧 Configuration Layer
│   │   ├── SecurityConfig.java            # Security & JWT configuration
│   │   └── SwaggerConfig.java             # API documentation config
│   │
│   ├── controller/                        # 🌐 Presentation Layer
│   │   ├── AuthController.java            # Authentication endpoints
│   │   └── UserController.java            # User management endpoints
│   │
│   ├── dto/                              # 📦 Data Transfer Objects
│   │   ├── AuthDTO.java                  # Authentication response
│   │   ├── ErrorDTO.java                 # Error response format
│   │   ├── LoginDTO.java                 # Login request
│   │   ├── RegisterDTO.java              # Registration request
│   │   ├── RoleDTO.java                  # Role representation
│   │   └── UserDTO.java                  # User representation
│   │
│   ├── service/                          # 🏛️ Application Layer
│   │   ├── AuthService.java              # Authentication business logic
│   │   ├── JwtService.java               # JWT token management
│   │   └── UserService.java              # User management logic
│   │
│   ├── entity/                           # 🏗️ Domain Layer
│   │   ├── User.java                     # User domain model
│   │   └── Role.java                     # Role domain model
│   │
│   ├── repository/                       # 💾 Infrastructure Layer
│   │   ├── UserRepository.java           # User data access
│   │   └── RoleRepository.java           # Role data access
│   │
│   ├── mapper/                           # 🔄 Mapping Layer
│   │   ├── AuthMapper.java               # Auth DTO mappings
│   │   ├── RoleMapper.java               # Role DTO mappings
│   │   └── UserMapper.java               # User DTO mappings
│   │
│   ├── security/                         # 🔐 Security Infrastructure
│   │   └── JwtAuthenticationFilter.java  # JWT validation filter
│   │
│   └── exception/                        # ⚠️ Exception Handling
│       ├── AuthException.java            # Base auth exception
│       ├── EmailAlreadyExistsException.java
│       ├── InvalidCredentialsException.java
│       ├── UserNotFoundException.java
│       ├── RoleNotFoundException.java
│       └── GlobalExceptionHandler.java   # Centralized error handling
│
├── test/java/com/auth/                   # 🧪 Test Layer
│   ├── config/                          # Test configurations
│   ├── controller/                      # Controller tests
│   ├── integration/                     # Integration tests
│   ├── repository/                      # Repository tests
│   └── service/                         # Service tests (unit)
│
└── resources/
    ├── application.properties            # Main configuration
    ├── application-test.properties       # Test configuration
    └── db/migration/                     # Database migrations
        ├── V1__Create_initial_tables.sql
        └── V2__Insert_initial_data.sql
```

### Camadas e Responsabilidades

#### 🌐 Presentation Layer (Controllers)
- **Responsabilidade**: Manipulação de HTTP requests/responses
- **Componentes**: `AuthController`, `UserController`
- **Padrões**: REST API, OpenAPI documentation, validation

#### 🏛️ Application Layer (Services)
- **Responsabilidade**: Lógica de negócio e orquestração
- **Componentes**: `AuthService`, `UserService`, `JwtService`
- **Padrões**: Transaction management, business rules

#### 🏗️ Domain Layer (Entities)
- **Responsabilidade**: Modelos de domínio e regras de negócio
- **Componentes**: `User`, `Role`
- **Padrões**: JPA entities, domain modeling

#### 💾 Infrastructure Layer (Repositories)
- **Responsabilidade**: Acesso a dados e persistência
- **Componentes**: `UserRepository`, `RoleRepository`
- **Padrões**: Spring Data JPA, query methods

## Qualidade de Código

### Padrões de Desenvolvimento
O projeto segue as melhores práticas de desenvolvimento Java e Spring Boot:

#### Java Moderno (Java 21)
- **Records**: Para DTOs e classes de dados imutáveis
- **Pattern Matching**: Simplificação de instanceof e switch
- **Type Inference**: Uso de `var` onde apropriado
- **Streams API**: Processamento de coleções funcional
- **Optional**: Tratamento de valores opcionais

#### Spring Boot Best Practices
- **Constructor Injection**: Injeção de dependências via construtor
- **Configuration Properties**: Configuração tipada com @ConfigurationProperties
- **Profile Management**: Separação de ambientes (dev, test, prod)
- **Transaction Management**: Boundaries transacionais apropriados

#### Arquitetura e Design
- **SOLID Principles**: Aplicação dos princípios SOLID
- **Clean Code**: Código legível e bem estruturado
- **Domain-Driven Design**: Modelagem orientada ao domínio
- **Exception Handling**: Tratamento centralizado de exceções

### Ferramentas de Qualidade (Recomendadas)

#### Análise Estática
```bash
# SonarQube - Análise de qualidade e segurança
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest

# Checkstyle - Verificação de estilo de código
mvn checkstyle:check

# PMD - Detecção de problemas de código
mvn pmd:check
```

#### Formatação e Linting
```bash
# Google Java Format
mvn com.google.fmt:fmt-maven-plugin:format

# SpotBugs - Detecção de bugs
mvn spotbugs:check
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

### Workflow de Desenvolvimento
Para contribuir com o projeto, siga estas etapas:

1. **Fork e Clone**
   ```bash
   git clone https://github.com/seu-usuario/auth.git
   cd auth
   ```

2. **Configuração do Ambiente**
   ```bash
   # Verificar Java 21
   java --version
   
   # Subir dependências
   docker-compose up -d postgres
   
   # Executar testes
   mvn test
   ```

3. **Desenvolvimento**
   ```bash
   # Criar branch para feature
   git checkout -b feature/nova-funcionalidade
   
   # Desenvolvimento incremental
   mvn spring-boot:run
   
   # Testes durante desenvolvimento
   mvn test -Dtest=*ServiceTest
   ```

4. **Validação**
   ```bash
   # Build completo
   mvn clean install
   
   # Verificação de qualidade
   mvn checkstyle:check spotbugs:check
   
   # Testes de integração
   mvn verify
   ```

### Extensão do Sistema

#### Adicionando Novos Endpoints
1. **Criar DTO** para request/response
2. **Implementar Controller** com validação
3. **Desenvolver Service** com lógica de negócio  
4. **Adicionar Repository** methods se necessário
5. **Configurar Segurança** se requerido
6. **Escrever Testes** unitários e integração

#### Adicionando Novos Roles
```java
// 1. Adicionar no enum RoleName
public enum RoleName {
    ADMIN, DEFAULT, MODERATOR // Novo role
}

// 2. Criar migration script
// V3__Add_moderator_role.sql

// 3. Configurar segurança
@PreAuthorize("hasRole('MODERATOR')")
public ResponseEntity<...> moderatorEndpoint() { ... }
```

### Monitoramento e Observabilidade

#### Health Checks
```bash
# Status da aplicação
curl http://localhost:8080/actuator/health

# Métricas da aplicação  
curl http://localhost:8080/actuator/metrics

# Informações da aplicação
curl http://localhost:8080/actuator/info
```

#### Logging
```properties
# Configuração de logs estruturados
logging.level.com.auth=DEBUG
logging.pattern.console=%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=logs/auth-service.log
```

#### Métricas Recomendadas
- **Micrometer**: Métricas de aplicação
- **Prometheus**: Coleta de métricas
- **Grafana**: Dashboards e visualização
- **ELK Stack**: Logs centralizados

## Recursos Úteis

## Recursos Úteis

### Documentação e APIs
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Actuator Endpoints**: `http://localhost:8080/actuator`
- **Health Check**: `http://localhost:8080/actuator/health`

### Coleções e Ferramentas
- **Postman Collection**: `Auth-Service-Collection.postman_collection.json`
- **Environment Variables**: `Auth-Service-Environment.postman_environment.json`
- **Docker Compose**: `docker-compose.yml` (PostgreSQL)
- **Database Migrations**: `src/main/resources/db/migration/`

### Links de Referência
- [Spring Boot 3.5.6 Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security 6 Reference](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - JWT debugger
- [MapStruct Documentation](https://mapstruct.org/)
- [Project Architecture Blueprint](Project_Architecture_Blueprint.md)

## Troubleshooting

### Problemas Comuns

#### 1. Erro de Conexão com Banco
```bash
# Verificar se PostgreSQL está rodando
docker-compose ps

# Verificar logs do banco
docker-compose logs postgres

# Restartar serviços
docker-compose down && docker-compose up -d postgres
```

#### 2. JWT Token Inválido
```bash
# Verificar configuração do secret
grep jwt.secret application.properties

# Regenerar token via login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","password":"segredo123"}'
```

#### 3. Testes Falhando
```bash
# Limpar e recompilar
mvn clean compile

# Executar testes específicos
mvn test -Dtest=AuthServiceTest

# Verificar configuração de teste
cat src/test/resources/application-test.properties
```

#### 4. Problemas de Build
```bash
# Verificar versão do Java
java --version  # Deve ser Java 21

# Limpar cache do Maven
mvn dependency:purge-local-repository

# Build com debug
mvn clean install -X
```

### Debug e Desenvolvimento

#### Executar em Modo Debug
```bash
# Maven com debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Conectar debugger na porta 5005
```

#### Logs Detalhados
```properties
# application-dev.properties
logging.level.org.springframework.security=DEBUG
logging.level.com.auth=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### Perfil de Desenvolvimento
```bash
# Executar com perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Variáveis de ambiente para desenvolvimento
export SPRING_PROFILES_ACTIVE=dev
export LOGGING_LEVEL_COM_AUTH=DEBUG
```

## Licença

Este projeto está sob a licença MIT. Consulte o arquivo LICENSE para mais detalhes.

---

## Próximas Melhorias

### 🚀 Roadmap Técnico
- [ ] **Cache Layer**: Implementar Redis para performance
- [ ] **Rate Limiting**: Proteção contra ataques de força bruta  
- [ ] **Audit Trail**: Log de ações dos usuários
- [ ] **Multi-tenant**: Suporte a múltiplos tenants
- [ ] **OAuth2 Integration**: Login social (Google, GitHub)
- [ ] **Event-Driven Architecture**: Domain events
- [ ] **API Versioning**: Versionamento de endpoints
- [ ] **Metrics Dashboard**: Métricas em tempo real

### 🧪 Melhorias de Teste
- [ ] **Contract Testing**: Testes de contrato de API
- [ ] **Performance Testing**: Testes de carga e stress
- [ ] **Mutation Testing**: Qualidade dos testes
- [ ] **TestContainers**: Testes com PostgreSQL real
- [ ] **E2E Testing**: Testes fim-a-fim automatizados

### 📊 Observabilidade
- [ ] **Distributed Tracing**: Jaeger/Zipkin integration
- [ ] **Application Metrics**: Micrometer + Prometheus
- [ ] **Log Aggregation**: ELK Stack integration
- [ ] **Health Checks**: Checks customizados de saúde
- [ ] **Alerting**: Alertas proativos de sistema

*Documento atualizado em: Setembro 2025*