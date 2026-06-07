package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.MaterialRepository;
import com.threeboys.config.ConnectionFactory;

public class MaterialDAO implements MaterialRepository {

	private final ConnectionFactory connectionFactory;

	public MaterialDAO(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
