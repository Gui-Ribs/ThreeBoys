package com.threeboys.application.control;

import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.application.repository.ProdutoRepository;
import com.threeboys.domain.model.Cliente;
import com.threeboys.domain.model.ItemPedido;
import com.threeboys.domain.model.Pedido;
import com.threeboys.domain.model.Produto;
import com.threeboys.domain.model.StatusPedido;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PedidoControl {

	private final PedidoRepository pedidoRepository;
	private final ClienteRepository clienteRepository;
	private final ProdutoRepository produtoRepository;

	public PedidoControl(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
			ProdutoRepository produtoRepository) {
		this.pedidoRepository = pedidoRepository;
		this.clienteRepository = clienteRepository;
		this.produtoRepository = produtoRepository;
	}

	public List<Pedido> list() {
		List<Pedido> pedidos = pedidoRepository.findAll();

		Map<Long, Cliente> clientesPorId = clienteRepository.findAll().stream()
				.collect(Collectors.toMap(Cliente::getId, Function.identity()));

		for (Pedido pedido : pedidos) {
			if (pedido.getCliente() != null) {
				Cliente completo = clientesPorId.get(pedido.getCliente().getId());
				if (completo != null) {
					pedido.setCliente(completo);
				}
			}
		}
		return pedidos;
	}

	public Optional<Pedido> search(long id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);
		pedido.ifPresent(this::hydrateCliente);
		return pedido;
	}

	public Pedido create(Pedido pedido) {
		valid(pedido);
		if (pedido.getStatusPedido() == null) {
			pedido.setStatusPedido(StatusPedido.PENDENTE);
		}
		return pedidoRepository.save(pedido);
	}

	public Pedido update(Pedido pedido) {
		if (pedido.getId() == null) {
			throw new IllegalArgumentException("Pedido sem id nao pode ser atualizado.");
		}
		valid(pedido);
		return pedidoRepository.update(pedido);
	}

	public void remove(long id) {
		pedidoRepository.delete(id);
	}

	public List<Cliente> clientes() {
		return clienteRepository.findAll();
	}

	public List<Produto> produtos() {
		return produtoRepository.findAll();
	}

	private void valid(Pedido pedido) {
		if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
			throw new IllegalArgumentException("Selecione um cliente.");
		}
		if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
			throw new IllegalArgumentException("Adicione ao menos um item.");
		}
		for (ItemPedido item : pedido.getItens()) {
			if (item.getProduto() == null || item.getProduto().getId() == null) {
				throw new IllegalArgumentException("Ha item sem produto selecionado.");
			}
			if (item.getQuantidade() <= 0) {
				throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
			}
			if (item.getPrecoUnitario() == null || item.getPrecoUnitario().signum() < 0) {
				throw new IllegalArgumentException("Preco do item invalido.");
			}
		}
	}

	private void hydrateCliente(Pedido pedido) {
		if (pedido.getCliente() == null) {
			return;
		}
		clienteRepository.findById(pedido.getCliente().getId()).ifPresent(pedido::setCliente);
	}

}
