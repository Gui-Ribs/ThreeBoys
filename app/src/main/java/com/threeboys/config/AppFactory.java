package com.threeboys.config;

import com.threeboys.application.control.ClienteControl;
import com.threeboys.application.control.PedidoControl;
import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.infrastructure.database.ClienteDAO;
import com.threeboys.infrastructure.database.PedidoDAO;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;

public final class AppFactory {

	// infra

	private final ConnectionFactory connectionFactory;
	private final JdbcExecutor jdbcExecutor;

	// Repository

	private final PedidoRepository pedidoRepository;
	private final ClienteRepository clienteRepository;

	// Control

	private final PedidoControl pedidoControl;
	private final ClienteControl clienteControl;

	private AppFactory() {
		this.connectionFactory = ConnectionFactory.getInstance();
		this.jdbcExecutor = new JdbcExecutor(connectionFactory);

		this.pedidoRepository = new PedidoDAO(jdbcExecutor);
		this.clienteRepository = new ClienteDAO(jdbcExecutor);

		this.pedidoControl = new PedidoControl(pedidoRepository, clienteRepository);
		this.clienteControl = new ClienteControl(clienteRepository);
	}

	public static AppFactory getInstance() {
		return Holder.INSTANCE;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void validConnection() {
		connectionFactory.valid();
	}

	// Control getters

	public ClienteControl getClienteControl() {
		return clienteControl;
	}

	public PedidoControl getPedidoControl() {
		return pedidoControl;
	}

	private static class Holder {
		private static final AppFactory INSTANCE = new AppFactory();
	}
}
