package com.threeboys.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

	private final DatabaseConfig config;

	private ConnectionFactory() {
		this.config = DatabaseConfig.getInstance();
	}

	public static ConnectionFactory getInstance() {
		return Holder.INSTANCE;
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
	}

	public boolean valid() {
		try (Connection connection = getConnection()) {
			return connection.isValid(2);
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao se conectar com o banco de dados", e);
		}
	}

	private static class Holder {
		private static final ConnectionFactory INSTANCE = new ConnectionFactory();
	}
}
