package com.auth.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class NoFlywayTestConfig {

    /**
     * This bean provides a Flyway migration strategy that does nothing,
     * effectively disabling Flyway migrations for tests.
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // do nothing - don't run migrations
        };
    }
}