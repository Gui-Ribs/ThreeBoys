package com.threeboys.application.repository;

import com.threeboys.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
	Optional<Usuario> findByLogin(String login);
}
