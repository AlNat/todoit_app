package dev.alnat.todoit.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

/**
 * Тестовая конфигурация с поднятием тест-контейнера
 *
 * Created by @author AlNat on 05.02.2022.
 * Licensed by Apache License, Version 2.0
 */
@Slf4j
@TestConfiguration
public class PostgreSQLTestContainerConfiguration {

    private static final String POSTGRES_DOCKER_NAME = "postgres:13.5-alpine3.15"; // Ожидается конкретная версия PG
    private static final String DRIVER_NAME = "org.postgresql.Driver";

    @Bean("datasource")
    @Primary
    public DataSource datasource() {
        final PostgreSQLContainer<?> postgreSqlContainer = getPostgreSQLContainer();

        log.info("PostgreSQL started in test-container. URL: [{}], username: [{}], password: [{}]",
                postgreSqlContainer.getJdbcUrl(),
                postgreSqlContainer.getUsername(),
                postgreSqlContainer.getPassword()
        );

        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(DRIVER_NAME);
        config.setPoolName("test-hikari-pool");
        config.setJdbcUrl(postgreSqlContainer.getJdbcUrl());
        config.setUsername(postgreSqlContainer.getUsername());
        config.setPassword(postgreSqlContainer.getPassword());
        config.setMaximumPoolSize(2);

        return new HikariDataSource(config);
    }

    private PostgreSQLContainer<?> getPostgreSQLContainer() {
        final PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>(POSTGRES_DOCKER_NAME);
        postgreSqlContainer.start();
        return postgreSqlContainer;
    }

}
