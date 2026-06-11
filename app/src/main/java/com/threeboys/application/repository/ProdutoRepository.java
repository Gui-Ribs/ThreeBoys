package com.threeboys.application.repository;

import com.threeboys.domain.model.Produto;
import java.util.List;

public interface ProdutoRepository {
	List<Produto> findAll();
	List<Produto> findByName(String nome);
	Produto update(Produto produto);
	Produto save(Produto produto);
	void delete(long id);
}
