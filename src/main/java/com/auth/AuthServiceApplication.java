package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication{

    public static void main(String[] args) {
        // Para definir perfis programaticamente, descomente as linhas abaixo
        // SpringApplication app = new SpringApplication(AuthServiceApplication.class);
        // app.setAdditionalProfiles("dev", "swagger");
        // app.run(args);
        
        // Execução padrão (usando configuração de perfis externa)
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
