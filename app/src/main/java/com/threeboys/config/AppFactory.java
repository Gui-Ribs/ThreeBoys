package com.threeboys.config;

public class AppFactory {

	private final ConnectionFactory connectionFactory = new ConnectionFactory();

	public ConnectionFactory connectionFactory() {
		return connectionFactory;
	}
}
