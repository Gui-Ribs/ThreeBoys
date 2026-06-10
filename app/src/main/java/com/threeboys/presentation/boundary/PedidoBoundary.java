package com.threeboys.presentation.boundary;

import com.threeboys.application.control.PedidoControl;
import com.threeboys.domain.model.ItemPedido;
import com.threeboys.domain.model.Pedido;
import com.threeboys.presentation.navigation.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class PedidoBoundary {

	private final PedidoControl control;
	private final SceneManager scenes;

	private final ObservableList<Pedido> pedidos = FXCollections.observableArrayList();
	private final ObservableList<ItemPedido> itens = FXCollections.observableArrayList();

	private final TableView<Pedido> tablePedidos = new TableView<>(pedidos);
	private final TableView<ItemPedido> tableItens = new TableView<>(itens);
	private final VBox root = new VBox(12);

	public PedidoBoundary(PedidoControl control, SceneManager scenes) {
		this.control = control;
		this.scenes = scenes;
		show();
		// load();
	}

	private void show() {
	}

	public Parent getView() {
		return root;
	}
}
