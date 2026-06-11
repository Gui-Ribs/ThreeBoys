package com.threeboys.config;

import com.threeboys.application.control.ClienteControl;
import com.threeboys.application.control.LoginControl;
import com.threeboys.application.control.MaterialControl;
import com.threeboys.application.control.PedidoControl;
import com.threeboys.application.control.ProdutoControl;
import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.application.repository.MaterialRepository;
import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.application.repository.ProdutoRepository;
import com.threeboys.application.repository.UsuarioRepository;
import com.threeboys.application.session.Session;
import com.threeboys.infrastructure.database.ClienteDAO;
import com.threeboys.infrastructure.database.MaterialDAO;
import com.threeboys.infrastructure.database.PedidoDAO;
import com.threeboys.infrastructure.database.ProdutoDAO;
import com.threeboys.infrastructure.database.UsuarioDAO;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;
import com.threeboys.infrastructure.security.BCryptEncoder;
import com.threeboys.application.session.Encoder;

public final class AppFactory {

	// infra

	private final ConnectionFactory connectionFactory;
	private final JdbcExecutor jdbcExecutor;

	// Session

	private final Session session = new Session();
    private final Encoder encoder = new BCryptEncoder();

	// Repository

	private final UsuarioRepository usuarioRepository;
	private final PedidoRepository pedidoRepository;
	private final ClienteRepository clienteRepository;
	private final ProdutoRepository produtoRepository;
	private final MaterialRepository materialRepository;

	// Control

	private final LoginControl loginControl;
	private final PedidoControl pedidoControl;
	private final ClienteControl clienteControl;
	private final ProdutoControl produtoControl;
	private final MaterialControl materialControl;

	private AppFactory() {
		this.connectionFactory = ConnectionFactory.getInstance();
		this.jdbcExecutor = new JdbcExecutor(connectionFactory);

		this.usuarioRepository = new UsuarioDAO(jdbcExecutor);
		this.pedidoRepository = new PedidoDAO(jdbcExecutor);
		this.clienteRepository = new ClienteDAO(jdbcExecutor);
		this.produtoRepository = new ProdutoDAO(jdbcExecutor);
		this.materialRepository = new MaterialDAO(jdbcExecutor);

		this.loginControl = new LoginControl(usuarioRepository, encoder, session);
		this.pedidoControl = new PedidoControl(pedidoRepository, clienteRepository, produtoRepository);
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

	public Session getSession() { 
		return session; 
	}
	
	public LoginControl getLoginControl() { 
		return loginControl; 
	}

	public Encoder encoder() {
		return encoder;
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

	public MaterialControl getMaterialControl() {
		return materialControl;
	}

	private static class Holder {
		private static final AppFactory INSTANCE = new AppFactory();
	}
}
