package com.threeboys.application.control;

import com.threeboys.application.repository.MaterialRepository;

public class MaterialControl {

	private final MaterialRepository materialRepository;

	public MaterialControl(MaterialRepository materialRepository) {
		this.materialRepository = materialRepository;
	}
}
