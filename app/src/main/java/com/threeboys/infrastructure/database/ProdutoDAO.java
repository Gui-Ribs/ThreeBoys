package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.ProdutoRepository;
import com.threeboys.config.ConnectionFactory;

public class ProdutoDAO implements ProdutoRepository {

	private final ConnectionFactory connectionFactory;

	public ProdutoDAO(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
