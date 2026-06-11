package com.threeboys.infrastructure.database;

import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.domain.model.Cliente;
import com.threeboys.domain.model.ItemPedido;
import com.threeboys.domain.model.Pedido;
import com.threeboys.domain.model.Produto;
import com.threeboys.domain.model.StatusPedido;
import com.threeboys.infrastructure.database.jdbc.JdbcExecutor;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PedidoDAO implements PedidoRepository {

	private final JdbcExecutor execute;

	public PedidoDAO(JdbcExecutor jdbcExecutor) {
		this.execute = jdbcExecutor;
	}

	// CRUD

	@Override
	public List<Pedido> findAll() {
		return execute.query("""
				SELECT id, cliente_id, valor_total, status_pedido,
					data_pedido, data_entrega, observacao
				FROM pedido
				ORDER BY data_pedido DESC
				""", ps -> {
		}, PedidoDAO::mapPedido);
	}

	@Override
	public Optional<Pedido> findById(long id) {
		Pedido pedido = execute.extract("""
				SELECT  p.id            AS id,
				        p.cliente_id,
				        p.valor_total,
				        p.status_pedido,
				        p.data_pedido,
				        p.data_entrega,
				        p.observacao,
				        i.id            AS item_id,
				        i.produto_id,
				        i.quantidade,
				        i.preco_unitario,
				        i.subtotal,
						pr.nome 		AS produto_nome
				FROM pedido p
				LEFT JOIN item_pedido i ON i.pedido_id = p.id
				LEFT JOIN produto pr on pr.id = i.produto_id
				WHERE p.id = ?
				""", ps -> ps.setLong(1, id), PedidoDAO::pedidoItens);
		return Optional.ofNullable(pedido);
	}

	@Override
	public Pedido save(Pedido pedido) {
		StatusPedido status = Objects.requireNonNull(pedido.getStatusPedido(),
				"status_pedido e NOT NULL no banco; defina o status antes de salvar.");

		execute.inTransaction(() -> {
			long pedidoId = execute.insertById("""
					INSERT INTO pedido (cliente_id, status_pedido, data_entrega, observacao)
					VALUES (?, ?, ?, ?)
					""", ps -> {
				ps.setLong(1, pedido.getCliente().getId());
				ps.setString(2, status.name());
				ps.setDate(3, pedido.getDataEntrega());
				ps.setString(4, pedido.getObservacao());
			});

			pedido.setId(pedidoId);

			for (ItemPedido item : pedido.getItens()) {
				item.setPedido(pedido);
				execute.update("""
						INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario)
						VALUES (?, ?, ?, ?)
						""", ps -> {
					ps.setLong(1, pedidoId);
					ps.setLong(2, item.getProduto().getId());
					ps.setInt(3, item.getQuantidade());
					ps.setBigDecimal(4, item.getPrecoUnitario());
				});
				item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
			}

			execute.queryObject("SELECT valor_total, data_pedido FROM pedido WHERE id = ?",
					ps -> ps.setLong(1, pedidoId), rs -> {
						BigDecimal vt = rs.getBigDecimal("valor_total");
						pedido.setValorTotal(vt == null ? BigDecimal.ZERO : vt);
						pedido.setDataPedido(rs.getDate("data_pedido"));
						return pedido;
					});
		});
		return pedido;
	}

	@Override
	public void delete(long id) {
		execute.inTransaction(() -> {
			execute.update("DELETE FROM item_pedido WHERE pedido_id = ?", ps -> ps.setLong(1, id));
			execute.update("DELETE FROM pedido WHERE id = ?", ps -> ps.setLong(1, id));
		});
	}

	@Override
	public Pedido update(Pedido pedido) {
		StatusPedido status = Objects.requireNonNull(pedido.getStatusPedido(),
				"status_pedido e NOT NULL; defina o status antes de salvar.");

		execute.inTransaction(() -> {
			execute.update("""
					UPDATE pedido
					SET cliente_id = ?, status_pedido = ?, data_entrega = ?, observacao = ?
					WHERE id = ?
					""", ps -> {
				ps.setLong(1, pedido.getCliente().getId());
				ps.setString(2, status.name());
				ps.setDate(3, pedido.getDataEntrega());
				ps.setString(4, pedido.getObservacao());
				ps.setLong(5, pedido.getId());
			});

			execute.update("DELETE FROM item_pedido WHERE pedido_id = ?", ps -> ps.setLong(1, pedido.getId()));

			for (ItemPedido item : pedido.getItens()) {
				item.setPedido(pedido);
				execute.update("""
						INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario)
						VALUES (?, ?, ?, ?)
						""", ps -> {
					ps.setLong(1, pedido.getId());
					ps.setLong(2, item.getProduto().getId());
					ps.setInt(3, item.getQuantidade());
					ps.setBigDecimal(4, item.getPrecoUnitario());
				});
				item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
			}

			execute.queryObject("SELECT valor_total FROM pedido WHERE id = ?", ps -> ps.setLong(1, pedido.getId()),
					rs -> {
						BigDecimal vt = rs.getBigDecimal("valor_total");
						pedido.setValorTotal(vt == null ? BigDecimal.ZERO : vt);
						return pedido;
					});
		});
		return pedido;
	}

	@Override
	public boolean existsByClienteId(long id) {
		return execute.queryObject("SELECT EXISTS(SELECT 1 FROM pedido WHERE cliente_id = ?)", ps -> ps.setLong(1, id),
				rs -> rs.getBoolean(1)).orElse(false);
	}

	// Helpers e validações

	private static Pedido pedidoItens(ResultSet rs) throws SQLException {
		Pedido pedido = null;
		List<ItemPedido> itens = new ArrayList<>();

		while (rs.next()) {
			if (pedido == null) {
				pedido = mapPedido(rs);
			}
			long itemId = rs.getLong("item_id");
			if (!rs.wasNull()) {
				ItemPedido item = mapItem(rs);
				item.setPedido(pedido);
				itens.add(item);
			}
		}
		if (pedido != null) {
			pedido.getItens().addAll(itens);
		}
		return pedido;
	}

	private static Pedido mapPedido(ResultSet rs) throws SQLException {
		Pedido p = new Pedido();
		p.setId(rs.getLong("id"));
		p.setCliente(clienteRef(rs.getLong("cliente_id")));
		p.setValorTotal(rs.getBigDecimal("valor_total"));
		p.setStatusPedido(readStatus(rs, "status_pedido"));
		p.setDataPedido(rs.getDate("data_pedido"));
		p.setDataEntrega(rs.getDate("data_entrega"));
		p.setObservacao(rs.getString("observacao"));
		return p;
	}

	private static ItemPedido mapItem(ResultSet rs) throws SQLException {
		ItemPedido item = new ItemPedido();
		item.setId(rs.getLong("item_id"));
		item.setProduto(produtoRef(rs.getLong("produto_id"), rs.getString("produto_nome")));
		item.setQuantidade(rs.getInt("quantidade"));
		item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
		item.setSubtotal(rs.getBigDecimal("subtotal")); // coluna gerada no banco
		return item;
	}

	private static StatusPedido readStatus(ResultSet rs, String coluna) throws SQLException {
		String valor = rs.getString(coluna);
		return valor == null ? null : StatusPedido.valueOf(valor);
	}

	private static Produto produtoRef(long produtoId, String nome) {
		Produto p = new Produto();
		p.setId(produtoId);
		p.setNome(nome);
		return p;
	}

	private static Cliente clienteRef(long clienteId) {
		Cliente c = new Cliente();
		c.setId(clienteId);
		return c;
	}
}
