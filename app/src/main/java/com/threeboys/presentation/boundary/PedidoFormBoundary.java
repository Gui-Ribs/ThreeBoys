package com.threeboys.presentation.boundary;

import com.threeboys.application.control.PedidoControl;
import com.threeboys.domain.model.Cliente;
import com.threeboys.domain.model.ItemPedido;
import com.threeboys.domain.model.Pedido;
import com.threeboys.domain.model.Produto;
import com.threeboys.domain.model.StatusPedido;
import com.threeboys.presentation.navigation.SceneManager;
import com.threeboys.presentation.util.Tasks;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class PedidoFormBoundary {

	private final PedidoControl control;
	private final SceneManager scenes;
	private final Long pedidoId; // null = novo

	private final ComboBox<Cliente> comboCliente = new ComboBox<>();
	private final ComboBox<StatusPedido> comboStatus = new ComboBox<>();
	private final DatePicker dataEntrega = new DatePicker();
	private final TextArea fieldObservacao = new TextArea();

	private final ComboBox<Produto> comboProduto = new ComboBox<>();
	private final Spinner<Integer> spinnerQtd = new Spinner<>(1, 9999, 1);
	private final TextField fieldPreco = new TextField();

	private final ObservableList<ItemPedido> itens = FXCollections.observableArrayList();
	private final TableView<ItemPedido> tabelaItens = new TableView<>(itens);
	private final Label labelTotal = new Label("Total: 0.00");

	private final VBox root = new VBox(12);

	public PedidoFormBoundary(PedidoControl control, SceneManager scenes, Long id) {
		this.control = control;
		this.scenes = scenes;
		this.pedidoId = id;
		show();
		loadReferences();
	}

	public Parent getView() {
		return root;
	}

	private void show() {
		configCombo(comboCliente, Cliente::getNome);
		configCombo(comboProduto, Produto::getNome);

		comboStatus.getItems().setAll(StatusPedido.values());
		comboStatus.setValue(StatusPedido.PENDENTE);
		spinnerQtd.setEditable(true);
		fieldObservacao.setPrefRowCount(2);

		comboProduto.valueProperty().addListener((o, antigo, prod) -> {
			if (prod != null) {
				fieldPreco.setText(BigDecimal.valueOf(prod.getPreco()).toPlainString());
			}
		});

		GridPane cab = new GridPane();
		cab.setHgap(8);
		cab.setVgap(8);
		cab.addRow(0, new Label("Cliente"), comboCliente);
		cab.addRow(1, new Label("Status"), comboStatus);
		cab.addRow(2, new Label("Entrega"), dataEntrega);
		cab.addRow(3, new Label("Observação"), fieldObservacao);

		Button addItem = new Button("Adicionar item");

		addItem.setOnAction(e -> loadItem());
		HBox entradaItem = new HBox(8, new Label("Produto"), comboProduto, new Label("Qtd"), spinnerQtd,
				new Label("Preço"), fieldPreco, addItem);

		buildTableItens();

		Button salvar = new Button("Salvar");
		salvar.setOnAction(e -> save());
		Button cancelar = new Button("Cancelar");
		cancelar.setOnAction(e -> scenes.pedidos()); // ajuste pro nome do seu metodo
		HBox acoes = new HBox(8, cancelar, salvar);

		VBox.setVgrow(tabelaItens, Priority.ALWAYS);
		root.setPadding(new Insets(16));
		root.getChildren().addAll(new Label(pedidoId == null ? "Novo pedido" : "Editar pedido"), cab, new Separator(),
				new Label("Itens"), entradaItem, tabelaItens, labelTotal, new Separator(), acoes);
	}

	private void buildTableItens() {
		TableColumn<ItemPedido, String> colProduto = new TableColumn<>("Produto");
		colProduto.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getProduto() == null || c.getValue().getProduto().getNome() == null
						? "?"
						: c.getValue().getProduto().getNome()));

		TableColumn<ItemPedido, String> colQtd = new TableColumn<>("Qtd");
		colQtd.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getQuantidade())));

		TableColumn<ItemPedido, String> colPreco = new TableColumn<>("Preço");
		colPreco.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getPrecoUnitario() == null ? "" : c.getValue().getPrecoUnitario().toPlainString()));

		TableColumn<ItemPedido, String> colSubtotal = new TableColumn<>("Subtotal");
		colSubtotal.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getSubtotal() == null ? "" : c.getValue().getSubtotal().toPlainString()));

		TableColumn<ItemPedido, Void> colRemover = new TableColumn<>("");
		colRemover.setCellFactory(col -> new TableCellRemover());

		tabelaItens.getColumns().addAll(List.of(colProduto, colQtd, colPreco, colSubtotal, colRemover));
		tabelaItens.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		itens.addListener((javafx.collections.ListChangeListener<ItemPedido>) c -> updateTotal());
	}

	private void loadReferences() {
		Tasks.run(control::clientes, lista -> comboCliente.getItems().setAll(lista), this::showErro);
		Tasks.run(control::produtos, lista -> comboProduto.getItems().setAll(lista), this::showErro);
		if (pedidoId != null) {
			Tasks.run(() -> control.search(pedidoId), this::insert, this::showErro);
		}
	}

	private void loadItem() {

		Produto produto = comboProduto.getValue();
		if (produto == null) {
			showErro(new IllegalArgumentException("Selecione um produto."));
			return;
		}
		spinnerQtd.increment(0);
		int qtd = spinnerQtd.getValue();

		BigDecimal preco = parsePreco(fieldPreco.getText());
		if (preco == null) {
			showErro(new IllegalArgumentException("Preço inválido."));
			return;
		}

		ItemPedido item = new ItemPedido();
		item.setProduto(produto);
		item.setQuantidade(qtd);
		item.setPrecoUnitario(preco);
		item.setSubtotal(preco.multiply(BigDecimal.valueOf(qtd)));
		itens.add(item);

		comboProduto.setValue(null);
		spinnerQtd.getValueFactory().setValue(1);
		fieldPreco.clear();
	}

	private void save() {
		Pedido pedido = new Pedido();

		if (pedidoId != null) {
			pedido.setId(pedidoId);
		}

		pedido.setCliente(comboCliente.getValue());
		pedido.setStatusPedido(comboStatus.getValue());
		LocalDate d = dataEntrega.getValue();
		pedido.setDataEntrega(d == null ? null : Date.valueOf(d));
		pedido.setObservacao(textOrNull(fieldObservacao.getText()));
		pedido.setItens(new ArrayList<>(itens));

		boolean novo = (pedidoId == null);
		Tasks.run(() -> novo ? control.create(pedido) : control.update(pedido), salvo -> scenes.pedidos(),
				this::showErro);
	}

	private void insert(Optional<Pedido> opt) {
		Pedido p = opt.orElse(null);

		if (p == null) {
			return;
		}

		comboCliente.setValue(p.getCliente());
		comboStatus.setValue(p.getStatusPedido());
		dataEntrega.setValue(p.getDataEntrega() == null ? null : p.getDataEntrega().toLocalDate());
		fieldObservacao.setText(nz(p.getObservacao()));
		itens.setAll(p.getItens());
	}

	private void updateTotalItens() {
		BigDecimal total = itens.stream().map(i -> i.getSubtotal() == null ? BigDecimal.ZERO : i.getSubtotal())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		labelTotal.setText("Total: " + total.toPlainString());
	}

	private static <T> void configCombo(ComboBox<T> combo, Function<T, String> name) {
		combo.setConverter(new StringConverter<>() {
			@Override
			public String toString(T t) {
				return t == null ? "" : name.apply(t);
			}
			@Override
			public T fromString(String s) {
				return null;
			}
		});

		combo.setCellFactory(lv -> new ListCell<>() {
			@Override
			protected void updateItem(T t, boolean empty) {
				super.updateItem(t, empty);
				setText(empty || t == null ? null : name.apply(t));
			}
		});
	}

	private static BigDecimal parsePreco(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		try {
			BigDecimal v = new BigDecimal(s.trim().replace(",", "."));
			return v.signum() < 0 ? null : v;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static String nz(String s) {
		return s == null ? "" : s;
	}

	private static String textOrNull(String s) {
		return (s == null || s.isBlank()) ? null : s;
	}

	private Object updateTotal() {
		throw new UnsupportedOperationException("Unimplemented method 'updateTotal'");
	}

	private void showErro(Throwable t) {
		String msg = (t == null || t.getMessage() == null) ? "Erro ao salvar." : t.getMessage();
		new Alert(Alert.AlertType.ERROR, msg).showAndWait();
	}

	private final class TableCellRemover extends TableCell<ItemPedido, Void> {

		private final Button botao = new Button("Remover");
		TableCellRemover() {
			botao.setOnAction(e -> itens.remove(getIndex()));
		}

		@Override
		protected void updateItem(Void item, boolean empty) {
			super.updateItem(item, empty);
			setGraphic(empty ? null : botao);
		}
	}

}
