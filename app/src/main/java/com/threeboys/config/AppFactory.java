package com.threeboys.config;

import com.threeboys.application.control.ClienteControl;
import com.threeboys.application.control.MaterialControl;
import com.threeboys.application.control.PedidoControl;
import com.threeboys.application.control.ProdutoControl;
import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.application.repository.MaterialRepository;
import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.application.repository.ProdutoRepository;
import com.threeboys.infrastructure.database.ClienteDAO;
import com.threeboys.infrastructure.database.MaterialDAO;
import com.threeboys.infrastructure.database.PedidoDAO;
import com.threeboys.infrastructure.database.ProdutoDAO;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;

public final class AppFactory {

	// infra

	private final ConnectionFactory connectionFactory;
	private final JdbcExecutor jdbcExecutor;

	// Repository

	private final PedidoRepository pedidoRepository;
	private final ClienteRepository clienteRepository;
	private final ProdutoRepository produtoRepository;
	private final MaterialRepository materialRepository;

	// Control

	private final PedidoControl pedidoControl;
	private final ClienteControl clienteControl;
	private final ProdutoControl produtoControl;
	private final MaterialControl materialControl;

	private AppFactory() {
		this.connectionFactory = ConnectionFactory.getInstance();
		this.jdbcExecutor = new JdbcExecutor(connectionFactory);

		this.pedidoRepository = new PedidoDAO(jdbcExecutor);
		this.clienteRepository = new ClienteDAO(jdbcExecutor);
		this.produtoRepository = new ProdutoDAO(jdbcExecutor);
		this.materialRepository = new MaterialDAO(jdbcExecutor);

		this.pedidoControl = new PedidoControl(pedidoRepository, clienteRepository);
		this.clienteControl = new ClienteControl(clienteRepository, pedidoRepository);
		this.produtoControl = new ProdutoControl(produtoRepository);
		this.materialControl = new MaterialControl(materialRepository);
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

	public ProdutoControl getProdutoControl() {
		return produtoControl;
	}

	public MaterialControl getMaterialControl()  {
		return materialControl;
	}

	private static class Holder {
		private static final AppFactory INSTANCE = new AppFactory();
	}
}
