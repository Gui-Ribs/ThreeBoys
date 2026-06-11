package com.threeboys.presentation.navigation;

import com.threeboys.application.session.Session;
import com.threeboys.config.AppFactory;
import com.threeboys.presentation.boundary.ClienteBoundary;
import com.threeboys.presentation.boundary.LoginBoundary;
import com.threeboys.presentation.boundary.MaterialBoundary;
import com.threeboys.presentation.boundary.PedidoBoundary;
import com.threeboys.presentation.boundary.PedidoFormBoundary;
import com.threeboys.presentation.boundary.ProdutoBoundary;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SceneManager {
	private final Stage stage;
	private final AppFactory factory;
	private final Session session;

	public SceneManager(Stage stage, AppFactory factory) {
		this.stage = stage;
		this.factory = factory;
		session = factory.getSession(); 
	}

	public boolean isAdmin()  { return session.isAdmin(); }

	public void login() {
		LoginBoundary boundary = new LoginBoundary(factory.getLoginControl(), this);
		changeScreen(boundary.getView(), "Login");
	}

	public void dashboard() {
		DashboardBoundary boundary = new DashboardBoundary(this);
		changeScreen(boundary.getView(), "Painel");
	}

	public void clientes() {
		if (!requireAdmin("Clientes")) {
        	return;
    	}
		ClienteBoundary boundary = new ClienteBoundary(factory.getClienteControl(), this);
		changeScreen(boundary.getView(), "Clientes");
	}

	public void pedidos() {
		PedidoBoundary boundary = new PedidoBoundary(factory.getPedidoControl(), this);
		changeScreen(boundary.getView(), "Pedidos");
	}

	public void createPedidos() {
		PedidoFormBoundary boundary = new PedidoFormBoundary(factory.getPedidoControl(), this, (Long) null);
		changeScreen(boundary.getView(), "Criar Pedido");
	}

	public void updatePedidos(long id) {
		PedidoFormBoundary boundary = new PedidoFormBoundary(factory.getPedidoControl(), this, id);
		changeScreen(boundary.getView(), "Criar Pedido");
	}

	public void material() {
		if (!requireAdmin("Materiais")) {
        	return;
    	}
		MaterialBoundary boundary = new MaterialBoundary(factory.getMaterialControl(), this);
		changeScreen(boundary.getView(), "Material");
	}

	public void produto() {
		if (!requireAdmin("Produtos")) {
        	return;
    	}
		ProdutoBoundary boundary = new ProdutoBoundary(factory.getProdutoControl(), this);
		changeScreen(boundary.getView(), "Produto");
	}

	public String usuarioLogado() {
    	return session.getUsuario() == null ? "" : session.getUsuario().getNome();
	}

	public void logout() {
    	factory.getLoginControl().logout();
    	login();
	}

	private boolean requireAdmin(String screen) {
		if (!session.isAdmin()) {
			accessDenied(screen);
			return false;
		}
    	return true;
	}

	private void accessDenied(String screen) {
		new Alert(Alert.AlertType.WARNING, String.format("Acesso restrito a administradores: %s", screen)).showAndWait();
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
