package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.config.ConnectionFactory;

public class ClienteDAO implements ClienteRepository {

	private final ConnectionFactory connectionFactory;

	public ClienteDAO(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
