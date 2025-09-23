# Swagger / OpenAPI

Este projeto utiliza `springdoc-openapi` para gerar a documentação da API e disponibilizar a UI do Swagger.

## Como acessar

- UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

As rotas acima estão liberadas pela configuração de segurança (`SecurityConfig`).

## Autenticação (Bearer JWT)

- Clique no botão "Authorize" no topo da UI
- Informe: `Bearer <seu_token_jwt>`
- O esquema de segurança é configurado em `com.auth.config.SwaggerConfig` como HTTP Bearer (JWT)

## Anotações nas Controllers

As controllers utilizam anotações do pacote `io.swagger.v3.oas.annotations` para descrever endpoints, respostas e schemas. Exemplos:

- `@Tag` para agrupar endpoints
- `@Operation` para título/descrição
- `@ApiResponses`/`@ApiResponse` para códigos de status e payloads
- `@SecurityRequirement` para exigir Bearer nos endpoints protegidos

## Configurações úteis

Arquivo `application.properties` inclui ajustes para a UI:

```properties
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.display-operation-id=true
```

## Dependência

Adicionada no `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

## Dicas

- Se a UI não abrir, verifique se a aplicação está na porta correta (`server.port`) e se o `SecurityFilterChain` libera `/swagger-ui/**` e `/v3/api-docs/**`.
- Para testar endpoints protegidos: gere um token via `/api/auth/login` e use o botão "Authorize" na UI.
