package com.threeboys.application.repository;

import com.threeboys.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
	List<Pedido> findAll();
	Optional<Pedido> findById(long id);
	Pedido save(Pedido pedido);
	Pedido update(Pedido pedido);
	void delete(long id);
	boolean existsByClienteId(long id);
}
