# Migração para Flyway

Este documento descreve a migração do sistema de inicialização de dados do `DataInitializer` para o Flyway, uma ferramenta de versionamento de banco de dados.

## O que foi alterado

### 1. Dependências Adicionadas
- `flyway-core`: Core do Flyway
- `flyway-database-postgresql`: Suporte específico para PostgreSQL

### 2. Configuração do Flyway
No `application.properties`:
```properties
# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate  # Mudou de create-drop para validate

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

### 3. Scripts de Migração
Criados em `src/main/resources/db/migration/`:

- **V1__Create_initial_tables.sql**: Cria as tabelas iniciais (users, roles, user_roles)
- **V2__Insert_initial_data.sql**: Insere os dados iniciais (roles e usuário admin)

### 4. DataInitializer Removido
O arquivo `DataInitializer.java` foi removido, pois sua funcionalidade foi substituída pelos scripts de migração do Flyway.

## Vantagens da Migração

1. **Performance**: Elimina consultas desnecessárias ao banco de dados a cada inicialização
2. **Versionamento**: Controle completo sobre as mudanças no banco de dados
3. **Rastreabilidade**: Histórico completo de todas as alterações
4. **Consistência**: Garante que todas as instâncias tenham o mesmo estado do banco
5. **Automação**: Migrações executadas automaticamente na inicialização

## Como Funciona

1. Na primeira execução, o Flyway cria a tabela `flyway_schema_history`
2. Os scripts são executados em ordem de versão (V1, V2, etc.)
3. Cada script é registrado na tabela de histórico
4. Execuções subsequentes só aplicam scripts novos

## Dados Iniciais

O usuário admin padrão:
- **Email**: admin@admin.com
- **Senha**: S&cr3T#120
- **Role**: ADMIN

## Comandos Úteis

### Verificar status das migrações
```bash
mvn flyway:info
```

### Executar migrações manualmente
```bash
mvn flyway:migrate
```

### Verificar migrações pendentes
```bash
mvn flyway:validate
```

## Estrutura dos Scripts

Os scripts seguem a convenção:
- `V{versão}__{descrição}.sql`
- Exemplo: `V1__Create_initial_tables.sql`

## Notas Importantes

- O Hibernate agora está configurado com `ddl-auto=validate`, ou seja, não cria nem modifica tabelas automaticamente
- Todas as mudanças no esquema devem ser feitas via scripts de migração do Flyway
- Os scripts são executados apenas uma vez por versão
- Use `ON CONFLICT DO NOTHING` para evitar erros em execuções subsequentes
