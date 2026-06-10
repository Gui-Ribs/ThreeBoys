package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.MaterialRepository;
import com.threeboys.domain.model.Material;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MaterialDAO implements MaterialRepository {

	private final JdbcExecutor execute;

	public MaterialDAO(JdbcExecutor jbdcExecutor) {
		this.execute = jbdcExecutor;
	}


	@Override
	public List<Material> findAll() {
		return execute.query("SELECT * FROM material ORDER BY nome",
				ps -> { }, // Sem parâmetros ou seja "?" em um WHERE
				MaterialDAO::mapMaterial);
	}

	@Override
	public List<Material> findByName(String nome) {
		return execute.query(
				"SELECT * FROM material WHERE nome LIKE ?",
				ps -> ps.setString(1, "%" + nome + "%"),
				MaterialDAO::mapMaterial
		);
	}

	@Override
	public Material save(Material material) {
		long id = execute.insertById(
				"INSERT INTO material (nome, qtde, unidadeMedida, marca, preco, estoque, descricao)" +
						"VALUES (?,?,?,?,?,?,?)",
				ps -> insert(ps, material)
		);
		material.setId(id);
		return material;
	}

	@Override
	public Material update(Material material) {
		execute.update(
				"UPDATE material " +
						"SET nome = ?, qtde = ?, unidadeMedida = ?, marca = ?, preco = ?, estoque = ?, descricao = ?" +
						"WHERE id = ?",
				ps -> {
					insert(ps, material);
					ps.setLong(8, material.getId());
				}
		);
		return material;
	}

	@Override
	public void delete(long id) {
		execute.update(
				"DELETE FROM material WHERE id = ?",
				ps -> ps.setLong(1, id));
	}

	private static void insert(PreparedStatement ps, Material m) throws SQLException {
		ps.setString(1, m.getNome());
		ps.setInt(2, m.getQtde());
		ps.setString(3, m.getUnidadeMedida());
		ps.setString(4, m.getMarca());
		ps.setDouble(5, m.getPreco());
		ps.setInt(6, m.getEstoque());
		ps.setString(7, m.getDescricao());
	}

	private static Material mapMaterial(ResultSet rs) throws SQLException {
		Material m = new Material();
		m.setId(rs.getLong("id"));
		m.setNome(rs.getString("nome"));
		m.setQtde(rs.getInt("qtde"));
		m.setUnidadeMedida(rs.getString("unidadeMedidda"));
		m.setMarca(rs.getString("marca"));
		m.setPreco(rs.getDouble("preco"));
		m.setEstoque(rs.getInt("estoque"));
		m.setDescricao((rs.getString("descricao")));
		return m;
	}
}
