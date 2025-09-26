package com.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
public class ProfileConfig {
    
    private static final Logger log = LoggerFactory.getLogger(ProfileConfig.class);
    
    private final Environment environment;
    
    public ProfileConfig(Environment environment) {
        this.environment = environment;
    }
    
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        log.info("Perfis ativos: {}", Arrays.toString(environment.getActiveProfiles()));
    }
}