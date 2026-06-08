package com.threeboys.config;

public final class AppFactory {

	private final ConnectionFactory connectionFactory;
	private final DatabaseConfig databaseConfig;

	private AppFactory() {
		this.connectionFactory = ConnectionFactory.getInstance();
		this.databaseConfig = DatabaseConfig.getInstance();
	}

	public static AppFactory getInstance(){
		return Holder.INSTANCE;
	}

	public DatabaseConfig getDatabaseConfig() {
		return databaseConfig;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void validConnection() {
		connectionFactory.valid();
	}

	private static class Holder {
		private static final AppFactory INSTANCE = new AppFactory();
	}
}
