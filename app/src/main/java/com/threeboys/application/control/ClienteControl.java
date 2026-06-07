package com.threeboys.application.control;

import com.threeboys.application.repository.ClienteRepository;

public class ClienteControl {

	private final ClienteRepository clienteRepository;

	public ClienteControl(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}
}
