package com.threeboys.application.control;

import com.threeboys.application.repository.UsuarioRepository;

public class LoginControl {

	private final UsuarioRepository usuarioRepository;

	public LoginControl(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
}
