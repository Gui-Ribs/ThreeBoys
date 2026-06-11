package com.threeboys.application.session;

import com.threeboys.domain.model.NivelAcesso;
import com.threeboys.domain.model.Usuario;

public class Session {

	private Usuario usuario;

	public void login(Usuario usuario) {
		this.usuario = usuario;
	}

	public void logout() {
		this.usuario = null;
	}

	public boolean isAuthenticated() {
		return usuario != null;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public NivelAcesso getRole() {
		return usuario == null ? null : usuario.getNivelAcesso();
	}

	public boolean isAdmin() {
		return getRole() == NivelAcesso.ADMIN;
	}
}
