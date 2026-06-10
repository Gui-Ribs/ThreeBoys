package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.ProdutoRepository;
import com.threeboys.domain.model.Produto;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProdutoDAO implements ProdutoRepository {

	private final JdbcExecutor execute;

	public ProdutoDAO(JdbcExecutor jbdcExecutor) {
		this.execute = jbdcExecutor;
	}

	@Override
	public List<Produto> findAll() {
		return execute.query("SELECT * FROM produto ORDER BY nome",
				ps -> { }, // Sem parâmetros ou seja "?" em um WHERE
				ProdutoDAO::mapProduto);
	}

	@Override
	public List<Produto> findByName(String nome) {
		return execute.query(
				"SELECT * FROM material WHERE nome LIKE ?",
				ps -> ps.setString(1, "%" + nome + "%"),
				ProdutoDAO::mapProduto
		);
	}

	@Override
	public Produto save(Produto produto) {
		long id = execute.insertById(
				"INSERT INTO produto (nome, tamanho, chocolate, tipo, preco, qtde, observacao)" +
						"VALUES (?,?,?,?,?,?,?)",
				ps -> insert(ps, produto)
		);
		produto.setId(id);
		return produto;
	}

	@Override
	public Produto update(Produto produto) {
		execute.update(
				"UPDATE produto " +
						"SET nome = ?, tamanho = ?, chocolate = ?, tipo = ?, preco = ?, qtde = ?, observacao = ?" +
						"WHERE id = ?",
				ps -> {
					insert(ps, produto);
					ps.setLong(8, produto.getId());
				}
		);
		return produto;
	}

	@Override
	public void delete(long id) {
		execute.update(
				"DELETE FROM produto WHERE id = ?",
				ps -> ps.setLong(1, id));
	}

	private static void insert(PreparedStatement ps, Produto p) throws SQLException {
		ps.setString(1, p.getNome());
		ps.setString(2, p.getTamanho());
		ps.setString(3, p.getChocolate());
		ps.setString(4, p.getTipo());
		ps.setDouble(5, p.getPreco());
		ps.setInt(6, p.getQtde());
		ps.setString(7, p.getObservacao());
	}

	private static Produto mapProduto(ResultSet rs) throws SQLException {
		Produto p = new Produto();
		p.setId(rs.getLong("id"));
		p.setNome(rs.getString("nome"));
		p.setQtde(rs.getInt("tamanho"));
		p.setChocolate(rs.getString("chocolate"));
		p.setTipo(rs.getString("tipo"));
		p.setPreco(rs.getDouble("preco"));
		p.setQtde(rs.getInt("qtde"));
		p.setObservacao((rs.getString("observacao")));
		return p;
	}
}
