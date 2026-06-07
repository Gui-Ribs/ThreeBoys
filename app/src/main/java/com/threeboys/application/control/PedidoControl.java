package com.threeboys.application.control;

import com.threeboys.application.repository.PedidoRepository;

public class PedidoControl {

	private final PedidoRepository pedidoRepository;

	public PedidoControl(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}
}
