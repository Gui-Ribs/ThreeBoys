package com.threeboys.application.control;

import com.threeboys.application.repository.ClienteRepository;
import com.threeboys.application.repository.PedidoRepository;
import com.threeboys.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public class ClienteControl {

	private final ClienteRepository repository;
	private final PedidoRepository pedidoRepository;

	public ClienteControl(ClienteRepository repository, PedidoRepository pedidoRepository) {
		this.repository = repository;
		this.pedidoRepository = pedidoRepository;
	}

	public List<Cliente> list() {
		return repository.findAll();
	}

	public Optional<Cliente> search(long id) {
		return repository.findById(id);
	}

	public Cliente save(Cliente cliente) {
		valid(cliente);
		return repository.save(cliente);
	}

	public Cliente update(Cliente cliente) {
		if (cliente.getId() == null) {
			throw new IllegalArgumentException("Não foi possível recuperar o cliente com o id nulo");
		}
		valid(cliente);
		return repository.update(cliente);
	}

	public void delete(long id) {
		if (pedidoRepository.existsByClienteId(id)) {
			throw new IllegalArgumentException("Não é possível excluir clientes que já possuam pedidos cadastrados");
		}
		repository.delete(id);
	}

	private void valid(Cliente cliente) {
		if (empty(cliente.getNome()) || cliente.getNome().length() > 50) {
			throw new IllegalArgumentException("O nome do cliente deve ser informado e ter no máximo 100 caracteres");
		}
		if (empty(cliente.getTelefone()) || cliente.getTelefone().length() > 20) {
			throw new IllegalArgumentException("O telefone deve ser informado e ter no máximo 20 caracteres");
		}
		if (cliente.getEndereco().length() > 100) {
			throw new IllegalArgumentException("Endereço deve ter no máximo 100 caracteres");
		}
	}

	private static boolean empty(String x) {
		return x == null || x.isBlank();
	}

}
