package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.config.ConnectionFactory;

public class PedidoDAO implements PedidoRepository {

	private final ConnectionFactory connectionFactory;

	public PedidoDAO(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
