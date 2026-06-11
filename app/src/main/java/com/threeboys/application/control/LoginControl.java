package com.threeboys.application.control;

import com.threeboys.application.repository.UsuarioRepository;
import com.threeboys.application.session.Encoder;
import com.threeboys.application.session.Session;
import com.threeboys.domain.model.Usuario;

public class LoginControl {

    private final UsuarioRepository repository;
    private final Encoder encoder;
    private final Session session;
 
    public LoginControl(UsuarioRepository repository, Encoder encoder, Session session) {
        this.repository = repository;
        this.encoder = encoder;
        this.session = session;
    }

	public Usuario authenticate(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Informe email e senha.");
        }
        Usuario usuario = repository.findByEmail(email.trim())
                .filter(u -> encoder.matches(senha, u.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos."));
 
        session.login(usuario);
        return usuario;
    }
    
    public void logout() {
        session.logout();
    }

}
