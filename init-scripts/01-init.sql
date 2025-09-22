-- Script de inicialização do banco de dados
-- Este script é executado automaticamente quando o container é criado pela primeira vez

-- Criar o banco de dados se não existir (já é criado pelo POSTGRES_DB)
-- CREATE DATABASE IF NOT EXISTS auth;

-- Conectar ao banco auth
\c auth;

-- Criar extensões se necessário
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Comentário para verificar se o script foi executado
DO $$
BEGIN
    RAISE NOTICE 'Banco de dados auth inicializado com sucesso!';
END $$;
