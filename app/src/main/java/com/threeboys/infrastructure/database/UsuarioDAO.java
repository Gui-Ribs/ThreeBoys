package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.UsuarioRepository;
import com.threeboys.config.ConnectionFactory;
import com.threeboys.domain.model.Usuario;
import java.util.Optional;

public class UsuarioDAO implements UsuarioRepository {

	private final ConnectionFactory connectionFactory;

	public UsuarioDAO(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public Optional<Usuario> findByLogin(String login) {
		return Optional.empty();
	}
}
