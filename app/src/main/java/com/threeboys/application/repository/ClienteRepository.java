package com.threeboys.application.repository;

import com.threeboys.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
	List<Cliente> findAll();
	Optional<Cliente> findById(long id);
	Cliente update(Cliente cliente);
	Cliente save(Cliente cliente);
	void delete(long id);
}
