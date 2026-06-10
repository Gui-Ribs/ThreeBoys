package com.threeboys.presentation.navigation;

import com.threeboys.config.AppFactory;
import com.threeboys.presentation.boundary.ClienteBoundary;
import com.threeboys.presentation.boundary.MaterialBoundary;
import com.threeboys.presentation.boundary.PedidoBoundary;
import com.threeboys.presentation.boundary.ProdutoBoundary;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
	private final Stage stage;
	private final AppFactory factory;

	public SceneManager(Stage stage, AppFactory factory) {
		this.stage = stage;
		this.factory = factory;
	}

	public void dashboard() {
		DashboardBoundary boundary = new DashboardBoundary(this);
        changeScreen(boundary.getView(), "Painel");
	}

	public void clientes() {
        ClienteBoundary boundary = new ClienteBoundary(factory.getClienteControl(), this);
        changeScreen(boundary.getView(), "Clientes");
    }

	public void pedidos() {
		PedidoBoundary boundary = new PedidoBoundary(factory.getPedidoControl(), this);
		changeScreen(boundary.getView(), "Pedidos");
	}

	public void material() {
		MaterialBoundary boundary = new MaterialBoundary(factory.getMaterialControl(), this);
		changeScreen(boundary.getView(), "Material");
	}
	
	public void produto() {
		ProdutoBoundary boundary = new ProdutoBoundary(factory.getProdutoControl(), this);
		changeScreen(boundary.getView(), "Produto");
	}

	private void changeScreen(Parent root, String titulo) {
		Scene scene = stage.getScene();
		if (scene == null) {
			stage.setScene(new Scene(root));
		} else {
			scene.setRoot(root);
		}
		stage.setTitle(titulo);
		if (!stage.isShowing()) {
			stage.show();
		}
	}

}
