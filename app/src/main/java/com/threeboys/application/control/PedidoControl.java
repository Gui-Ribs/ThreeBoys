package com.threeboys.application.control;

import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.domain.model.Cliente;
import com.threeboys.domain.model.Pedido;
import com.threeboys.domain.model.StatusPedido;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PedidoControl {

	private final PedidoRepository pedidoRepository;
	private final ClienteRepository clienteRepository;

	public PedidoControl(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
		this.pedidoRepository = pedidoRepository;
		this.clienteRepository = clienteRepository;
	}

	public List<Pedido> listar() {
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

	public Optional<Pedido> buscar(long id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);
		pedido.ifPresent(this::hidratarCliente);
		return pedido;
	}

	public Pedido criar(Pedido pedido) {
		validar(pedido);
		if (pedido.getStatusPedido() == null) {
			pedido.setStatusPedido(StatusPedido.PENDENTE);
		}
		return pedidoRepository.save(pedido);
	}

	public void remover(long id) {
		pedidoRepository.delete(id);
	}

	private void validar(Pedido pedido) {
		if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
			throw new IllegalArgumentException("Pedido precisa de um cliente válido.");
		}
		if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
			throw new IllegalArgumentException("Pedido precisa de ao menos um item.");
		}
	}

	private void hidratarCliente(Pedido pedido) {
		if (pedido.getCliente() == null) {
			return;
		}
		clienteRepository.findById(pedido.getCliente().getId()).ifPresent(pedido::setCliente);
	}

}
