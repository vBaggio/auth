-- V2__Insert_initial_data.sql
-- Script de migração para inserir dados iniciais (roles e usuário admin)

-- Inserir roles iniciais se não existirem
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('DEFAULT') ON CONFLICT (name) DO NOTHING;

-- Inserir usuário admin se não existir
-- A senha é 'segredo123' codificada com BCrypt (cost 10)
INSERT INTO users (email, password, first_name, last_name) 
VALUES (
    'admin@admin.com', 
    '$2a$10$o9Z1AOQGqLpgFyCCPAeqNOddWj36gRG7khAbJAj/apHe4fHZogrvu', 
    'Admin', 
    'User'
) ON CONFLICT (email) DO NOTHING;

-- Associar o usuário admin ao role ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'admin@admin.com' 
  AND r.name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Comentário para verificar se a migração foi executada
DO $$
BEGIN
    RAISE NOTICE 'Migração V2 - Dados iniciais inseridos com sucesso!';
    RAISE NOTICE 'Usuário admin criado: admin@admin.com';
    RAISE NOTICE 'Senha: segredo123';
END $$;
