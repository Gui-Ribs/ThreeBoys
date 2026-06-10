package com.threeboys.infrastructure.database.jdbc;

import com.threeboys.config.ConnectionFactory;
import com.threeboys.infrastructure.database.jdbc.helpers.DataAccessException;
import com.threeboys.infrastructure.database.jdbc.helpers.DataSetExtractor;
import com.threeboys.infrastructure.database.jdbc.helpers.RowMapper;
import com.threeboys.infrastructure.database.jdbc.helpers.StatementPreparer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExecutor {

	private static final System.Logger LOGGER = System.getLogger(JdbcExecutor.class.getName());

	private ConnectionFactory connectionFactory;

	private final ThreadLocal<Connection> transaction = new ThreadLocal<>();

	public JdbcExecutor(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	// Query Simples

	public <T> List<T> query(String sql, StatementPreparer preparer, RowMapper<T> mapper) {
		Connection cn = null;

		try {
			cn = getConnection();
			try (PreparedStatement ps = cn.prepareStatement(sql)) {
				preparer.prepare(ps); // Trata parâmetros "?"

				try (ResultSet rs = ps.executeQuery()) {
					return mapRows(rs, mapper);
				}
			}

		} catch (SQLException e) {
			throw new DataAccessException(String.format("Erro ao executar a query: %s", sql), e);
		} finally {
			release(cn);
		}
	}

	// Usado principalmente para encontrar byId

	public <T> Optional<T> queryObject(String sql, StatementPreparer preparer, RowMapper<T> mapper) {
		List<T> results = query(sql, preparer, mapper);

		if (results.isEmpty()) {
			return Optional.empty();
		}

		if (results.size() > 1) {
			throw new DataAccessException(String.format("Resultado encontrado acima do esperado: %s", sql));
		}

		return Optional.of(results.get(0));
	}

	// Usado para extrair várias linhas em um objeto (JOIN)

	public <T> T extract(String sql, StatementPreparer preparer, DataSetExtractor<T> extractor) {
		Connection cn = null;

		try {
			cn = getConnection();
			try (PreparedStatement ps = cn.prepareStatement(sql)) {
				preparer.prepare(ps);

				try (ResultSet rs = ps.executeQuery()) {
					return extractor.extract(rs);
				}
			}

		} catch (SQLException e) {
			throw new DataAccessException(String.format("Erro ao executar a query: %s", sql), e);
		} finally {
			release(cn);
		}
	}

	// Create, Update e Delete

	public int update(String sql, StatementPreparer preparer) {
		Connection cn = null;
		String format = sql.strip().split("\\s+", 2)[0].toUpperCase();
		try {
			cn = getConnection();
			try (PreparedStatement ps = cn.prepareStatement(sql)) {
				preparer.prepare(ps);
				return ps.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DataAccessException(String.format("Erro ao executar: %s", format), e);
		} finally {
			release(cn);
		}
	}

	// INSERT que retorna o Id do banco

	public long insertById(String sql, StatementPreparer preparer) {
		Connection cn = null;

		try {
			cn = getConnection();
			try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				preparer.prepare(ps);
				ps.executeUpdate();

				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (keys.next()) {
						return keys.getLong(1);
					}
					throw new DataAccessException(String.format("Nenhuma chave gerada: %s", sql));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(String.format("Erro ao inserir: %s", sql), e);
		} finally {
			release(cn);
		}
	}

	// Realiza o map do ResultSet (Retorno do banco) em um objeto java

	private <T> List<T> mapRows(ResultSet rs, RowMapper<T> mapper) throws SQLException {
		List<T> result = new ArrayList<>();
		while (rs.next()) {
			result.add(mapper.map(rs));
		}
		return result;
	}

	// Tratamento da transaction, reutiliza a thread caso exista, tratamento do
	// rollback e commit da transaction ao final

	public void inTransaction(Runnable work) {
		if (transaction.get() != null) {
			work.run();
			return;
		}

		Connection cn;
		try {
			cn = connectionFactory.getConnection();
		} catch (SQLException e) {
			throw new DataAccessException(String.format("Erro na conexão para a transação %s", e));
		}

		try {
			cn.setAutoCommit(false);
			transaction.set(cn);
			work.run();
			cn.commit();
		} catch (SQLException e) {
			rollback(cn);
			throw new DataAccessException(String.format("Transação Revertida, erro na execução: %s", e));
		} catch (RuntimeException | Error e) {
			rollback(cn);
			throw e;
		} finally {
			transaction.remove();
			commit(cn);
		}
	}

	// Responsável por salvar as alterações

	private void commit(Connection cn) {
		try {
			cn.setAutoCommit(true);
		} catch (SQLException e) {
			LOGGER.log(System.Logger.Level.ERROR, String.format("Falha ao fechar conexão da transaction %s", e));
		}
	}

	// Responsável por abortar a operação caso dê algo errado

	private void rollback(Connection cn) {
		try {
			cn.rollback();
		} catch (SQLException e) {
			LOGGER.log(System.Logger.Level.ERROR, String.format("Falha no rollback da transaction %s", e));
		}
	}

	// Liberar conexão

	private void release(Connection cn) {
		if (cn == null || cn == transaction.get()) {
			return;
		}

		try {
			cn.close();
		} catch (SQLException e) {
			LOGGER.log(System.Logger.Level.WARNING, "Falha ao fechar conexão", e);
		}
	}

	// Responsável por pegar a conexão ao banco, podendo ser transacional ou simples

	private Connection getConnection() throws SQLException {
		Connection tc = transaction.get();
		return (tc != null) ? tc : connectionFactory.getConnection();
	}
}
