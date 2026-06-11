package com.threeboys.application.repository;

import com.threeboys.domain.model.Material;
import java.util.List;
import java.util.Optional;

public interface MaterialRepository {
	List<Material> findAll();
	List<Material> findByName(String nome);
	Material update(Material material);
	Material save(Material material);
	void delete(long id);
}
