package com.example.botpoliclinica;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest
public class AbstractIntegrationTest {
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:11.3");

    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
        // Установка переменных окружения для базы данных
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }

    @Autowired
    private DataSource dataSource;

    @Test
    public void contextLoads() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
        }
    }
}
