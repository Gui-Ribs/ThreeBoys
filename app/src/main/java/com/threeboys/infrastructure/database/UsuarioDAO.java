package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.UsuarioRepository;
import com.threeboys.domain.model.NivelAcesso;
import com.threeboys.domain.model.Usuario;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioDAO implements UsuarioRepository {

	private final JdbcExecutor execute;

	public UsuarioDAO(JdbcExecutor jdbcExecutor) {
		this.execute = jdbcExecutor;
	}

	@Override
	public Optional<Usuario> findByEmail(String email) {
		return execute.queryObject("SELECT id, name, email, password, role FROM user WHERE email = ?",
			ps -> ps.setString(1, email), UsuarioDAO::mapUsuario);
	}

	private static Usuario mapUsuario(ResultSet rs) throws SQLException {
		Usuario u = new Usuario();
		u.setId(rs.getLong("id"));
		u.setNome(rs.getString("name"));
		u.setEmail(rs.getString("email"));
		u.setPassword(rs.getString("password")); // hash BCrypt, só sai daqui pra verificação
		u.setNivelAcesso(readRole(rs, "role"));
		return u;
	}

	private static NivelAcesso readRole(ResultSet rs, String coluna) throws SQLException {
		String valor = rs.getString(coluna);
		return valor == null ? null : NivelAcesso.valueOf(valor);
	}
}
