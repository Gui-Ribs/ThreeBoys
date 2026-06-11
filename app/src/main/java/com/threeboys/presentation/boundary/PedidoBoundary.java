package com.threeboys.presentation.boundary;

import com.threeboys.application.control.PedidoControl;
import com.threeboys.domain.model.ItemPedido;
import com.threeboys.domain.model.Pedido;
import com.threeboys.infrastructure.database.jdbc.helpers.DataAccessException;
import com.threeboys.presentation.navigation.SceneManager;
import com.threeboys.presentation.util.Tasks;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
		loadPedidos();
	}

	public Parent getView() {
		return root;
	}

	private void show() {
		buildTablePedidos();
		buildTableItens();

		Button back = new Button("‹ Painel");
		back.setOnAction(e -> scenes.dashboard());

		Button refresh = new Button("Refresh");
		refresh.setOnAction(e -> loadPedidos());

		Button remove = new Button("Remover");
		remove.setOnAction(e -> removeSelected());

		Button create = new Button("Criar");
		create.setOnAction(e -> scenes.createPedidos());

		Button update = new Button("Atualizar");
		update.setOnAction(e -> editSelected());

		update.disableProperty().bind(tablePedidos.getSelectionModel().selectedItemProperty().isNull());

		// só habilita o botão quando há um pedido selecionado
		remove.disableProperty().bind(tablePedidos.getSelectionModel().selectedItemProperty().isNull());

		HBox acoes = new HBox(8, back, refresh, create, update, remove);

		VBox.setVgrow(tablePedidos, Priority.ALWAYS);
		root.setPadding(new Insets(16));
		root.getChildren().addAll(tablePedidos, acoes, new Label("Itens do pedido selecionado"), tableItens);
	}

	private void editSelected() {
		Pedido sel = tablePedidos.getSelectionModel().getSelectedItem();
		if (sel != null && sel.getId() != null) {
			scenes.updatePedidos(sel.getId());
		}
	}

	private void buildTablePedidos() {
		TableColumn<Pedido, String> colCliente = new TableColumn<>("Cliente");
		colCliente.setCellValueFactory(c -> new ReadOnlyStringWrapper(nomeCliente(c.getValue())));

		TableColumn<Pedido, String> colStatus = new TableColumn<>("Status");
		colStatus.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getStatusPedido() == null ? "" : c.getValue().getStatusPedido().name()));

		TableColumn<Pedido, String> colData = new TableColumn<>("Data");
		colData.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getDataPedido() == null ? "" : c.getValue().getDataPedido().toString()));

		TableColumn<Pedido, String> colTotal = new TableColumn<>("Total");
		colTotal.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getValorTotal() == null ? "0.00" : c.getValue().getValorTotal().toPlainString()));

		tablePedidos.getColumns().addAll(List.of(colCliente, colStatus, colData, colTotal));
		tablePedidos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tablePedidos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
			if (novo == null) {
				itens.clear();
			} else {
				loadItens(novo.getId());
			}
		});
	}

	private void buildTableItens() {
		TableColumn<ItemPedido, String> colProduto = new TableColumn<>("Produto");
		colProduto.setCellValueFactory(c -> new ReadOnlyStringWrapper(nomeProduto(c.getValue())));

		TableColumn<ItemPedido, String> colQtd = new TableColumn<>("Qtd");
		colQtd.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getQuantidade())));

		TableColumn<ItemPedido, String> colPreco = new TableColumn<>("Preço unit.");
		colPreco.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getPrecoUnitario() == null ? "" : c.getValue().getPrecoUnitario().toPlainString()));

		TableColumn<ItemPedido, String> colSubtotal = new TableColumn<>("Subtotal");
		colSubtotal.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getSubtotal() == null ? "" : c.getValue().getSubtotal().toPlainString()));

		tableItens.getColumns().addAll(List.of(colProduto, colQtd, colPreco, colSubtotal));
		tableItens.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void loadPedidos() {
		Tasks.run(control::list, pedidos::setAll, this::showErro);
	}

	private void loadItens(long pedidoId) {
		Tasks.run(() -> control.search(pedidoId), opt -> itens.setAll(opt.map(Pedido::getItens).orElseGet(List::of)),
				this::showErro);
	}

	private void removeSelected() {
		Pedido selecionado = tablePedidos.getSelectionModel().getSelectedItem();
		if (selecionado == null) {
			return;
		}
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Remover o pedido selecionado?", ButtonType.YES,
				ButtonType.NO);
		confirm.setTitle("Confirmar remoção");

		Optional<ButtonType> resposta = confirm.showAndWait();
		if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
			Tasks.runVoid(() -> control.remove(selecionado.getId()), this::loadPedidos, this::showErro);
		}
	}

	private static String nomeCliente(Pedido p) {
		if (p.getCliente() == null || p.getCliente().getNome() == null) {
			return "(sem cliente)";
		}
		return p.getCliente().getNome();
	}

	private static String nomeProduto(ItemPedido i) {
		if (i.getProduto() == null) {
			return "?";
		}
		return i.getProduto().getNome() != null ? i.getProduto().getNome() : "#" + i.getProduto().getId();
	}

	private void showErro(Throwable t) {
		String msg = (t instanceof DataAccessException)
				? "Erro de acesso ao banco: " + t.getMessage()
				: "Erro inesperado: " + (t == null ? "desconhecido" : t.getMessage());
		new Alert(Alert.AlertType.ERROR, msg).showAndWait();
	}
}
