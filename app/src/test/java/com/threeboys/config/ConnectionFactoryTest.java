package com.threeboys.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Tag("integration")
class ConnectionFactoryTest {

    @Test
    void shouldOpenConnection() throws SQLException {
        System.out.println("MYSQL_HOST = " + System.getenv("MYSQL_HOST"));
        ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
        try (Connection connection = connectionFactory.getConnection()) {
            assertTrue(connection.isValid(2));
        }
    }

    @Test
    void shouldExecuteQuery() throws SQLException {
        ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
        String sql = "SELECT 1";
        try (
            Connection connection = connectionFactory.getConnection(); 
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
        }
    }
}
