package ru.ukhanov.t1.java.aspects.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
public class DbConfig {
    private final DataSource dataSource;

    @PostConstruct
    public void ensureTableExists() {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData dbMeta = conn.getMetaData();
            try (ResultSet rs = dbMeta.getTables(null, null, "data_source_error_log", null)) {
                if (!rs.next()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(
                                "CREATE TABLE data_source_error_log (" +
                                        "id BIGINT PRIMARY KEY, " +
                                        "error_message TEXT, " +
                                        "stack_trace TEXT, " +
                                        "timestamp TIMESTAMP" +
                                        ");"
                        );
                        System.out.println("Created table data_source_error_log");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to check/create table data_source_error_log: " + e.getMessage());
        }
    }
}
