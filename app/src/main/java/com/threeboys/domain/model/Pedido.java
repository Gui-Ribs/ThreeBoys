package com.threeboys.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

	private Long id;
	private Cliente cliente;
	private List<ItemPedido> itens = new ArrayList<>();
}
