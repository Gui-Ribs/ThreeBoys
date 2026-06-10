package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.domain.model.Cliente;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClienteDAO implements ClienteRepository {

	private final JdbcExecutor execute;

	public ClienteDAO(JdbcExecutor jdbcExecutor) {
		this.execute = jdbcExecutor;
	}

	@Override
	public List<Cliente> findAll() {
		return execute.query("SELECT id, nome, telefone, endereco , observacao FROM cliente ORDER BY nome", 
		ps -> { }, // Sem parâmetros ou seja "?" em um WHERE
		ClienteDAO::mapCliente);
	}

	@Override
	public Optional<Cliente> findById(long id) {
		return execute.queryObject(
			"SELECT id, nome, telefone, endereco, observacao FROM cliente WHERE id = ?",
			ps -> ps.setLong(1, id), 
			ClienteDAO::mapCliente);
	}

	@Override
	public Cliente save(Cliente cliente) {
		long id = execute.insertById(
			"INSERT INTO cliente (nome, telefone, endereco, observacao) VALUES (?,?,?,?)", 
			ps -> insert(ps, cliente)
		);
		cliente.setId(id);
		return cliente;
	}

	@Override
	public Cliente update(Cliente cliente) {
		execute.update(
			"UPDATE cliente SET nome = ?, telefone = ?, endereco = ?, observacao = ? WHERE id = ?",
			ps -> {
				insert(ps, cliente);
				ps.setLong(5, cliente.getId());
			});
		return cliente;
	}

	@Override
	public void delete(long id) {
		execute.update("DELETE FROM cliente WHERE id = ?", ps -> ps.setLong(1, id));
	}

	// Mappers

	private static void insert(PreparedStatement ps, Cliente c) throws SQLException {
        ps.setString(1, c.getNome());
        ps.setString(2, c.getTelefone());
        ps.setString(3, c.getEndereco());
        ps.setString(4, c.getObservacao());
    }

	private static Cliente mapCliente(ResultSet rs) throws SQLException {
		Cliente c = new Cliente();
		c.setId(rs.getLong("id"));
		c.setNome(rs.getString("nome"));
		c.setTelefone(rs.getString("telefone"));
		c.setEndereco(rs.getString("endereco"));
		c.setObservacao(rs.getString("observacao"));
		return c;
	}

}
