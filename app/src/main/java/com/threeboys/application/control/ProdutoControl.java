package com.threeboys.application.control;

import com.threeboys.application.repository.ProdutoRepository;

public class ProdutoControl {

	private final ProdutoRepository produtoRepository;

	public ProdutoControl(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}
}
