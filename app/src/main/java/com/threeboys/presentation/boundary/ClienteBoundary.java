package com.threeboys.presentation.boundary;

import com.threeboys.application.control.ClienteControl;
import com.threeboys.domain.model.Cliente;
import com.threeboys.domain.model.Contato;
import com.threeboys.domain.model.Pagamento;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ClienteBoundary {

	private final ClienteControl control;
	private final SceneManager scenes;

	private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();
	private final TableView<Cliente> tabela = new TableView<>(clientes);

	private final TextField fieldNome = new TextField();
	private final TextField fieldTelefone = new TextField();
	private final TextField fieldEndereco = new TextField();
	private final ComboBox<Contato> comboContato = new ComboBox<>();
	private final ComboBox<Pagamento> comboPagamento = new ComboBox<>();
	private final TextArea fieldObservacao = new TextArea();

	private Cliente newCliente;

	private final VBox root = new VBox(12);

	public ClienteBoundary(ClienteControl control, SceneManager scenes) {
		this.control = control;
		this.scenes = scenes;
		show();
		load();
	}

	public Parent getView() {
		return root;
	}

	private void show() {
		buildTable();
		fieldObservacao.setPrefRowCount(2);

		comboContato.getItems().setAll(Contato.values());
		comboPagamento.getItems().setAll(Pagamento.values());

		GridPane form = new GridPane();
		form.setHgap(8);
		form.setVgap(8);
		form.addRow(0, new Label("Nome"), fieldNome);
		form.addRow(1, new Label("Telefone"), fieldTelefone);
		form.addRow(2, new Label("Endereço"), fieldEndereco);
		form.addRow(3, new Label("Preferencia por Contato"), comboContato);
		form.addRow(4, new Label("Preferencia por Pagamento"), comboPagamento);
		form.addRow(5, new Label("Observação"), fieldObservacao);

		Button back = new Button("‹ Painel");
		back.setOnAction(e -> scenes.dashboard());

		Button newRegister = new Button("Novo");
		newRegister.setOnAction(e -> clean());

		Button save = new Button("Salvar");
		save.setOnAction(e -> save());

		Button remove = new Button("Remover");
		remove.setOnAction(e -> removeSelected());
		remove.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());

		HBox acoes = new HBox(8, back, newRegister, save, remove);

		VBox.setVgrow(tabela, Priority.ALWAYS);
		root.setPadding(new Insets(16));
		root.getChildren().addAll(tabela, new Separator(), form, acoes);
	}

	private void buildTable() {
		TableColumn<Cliente, String> colNome = new TableColumn<>("Nome");
		colNome.setCellValueFactory(c -> new ReadOnlyStringWrapper(nz(c.getValue().getNome())));

		TableColumn<Cliente, String> colTelefone = new TableColumn<>("Telefone");
		colTelefone.setCellValueFactory(c -> new ReadOnlyStringWrapper(nz(c.getValue().getTelefone())));

		TableColumn<Cliente, String> colEndereco = new TableColumn<>("Endereço");
		colEndereco.setCellValueFactory(c -> new ReadOnlyStringWrapper(nz(c.getValue().getEndereco())));

		TableColumn<Cliente, String> colContato = new TableColumn<>("Contato");
		colContato.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getPrefContato() == null ? "" : c.getValue().getPrefContato().name()));

		TableColumn<Cliente, String> colPagamento = new TableColumn<>("Pagamento");
		colPagamento.setCellValueFactory(c -> new ReadOnlyStringWrapper(
				c.getValue().getPrefPagamento() == null ? "" : c.getValue().getPrefPagamento().name()));

		tabela.getColumns().addAll(List.of(colNome, colTelefone, colEndereco, colContato, colPagamento));
		tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // TODO: Atualizar, não recomendo usar, é só
																			// teste

		tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
			if (novo != null) {
				populateForm(novo);
			}
		});
	}

	private void load() {
		Tasks.run(control::list, clientes::setAll, this::showErro);
	}

	private void save() {
		Cliente cliente = new Cliente();
		if (newCliente != null) {
			cliente.setId(newCliente.getId());
		}
		cliente.setNome(fieldNome.getText());
		cliente.setTelefone(fieldTelefone.getText());
		cliente.setEndereco(textOrNull(fieldEndereco.getText()));
		cliente.setPrefContato(comboContato.getValue());
		cliente.setPrefPagamento(comboPagamento.getValue());
		cliente.setObservacao(textOrNull(fieldObservacao.getText()));

		boolean novo = (newCliente == null);
		Tasks.run(() -> novo ? control.save(cliente) : control.update(cliente), salvo -> {
			clean();
			load();
		}, this::showErro); // validação do Control nome & telefone cai aqui como um Alert
	}

	private void removeSelected() {
		Cliente selecionado = tabela.getSelectionModel().getSelectedItem();
		if (selecionado == null) {
			return;
		}
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Remover \"" + nz(selecionado.getNome()) + "\"?",
				ButtonType.YES, ButtonType.NO);
		confirm.setTitle("Confirmar remoção");

		Optional<ButtonType> resposta = confirm.showAndWait();
		if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
			Tasks.runVoid(() -> control.delete(selecionado.getId()), () -> {
				clean();
				load();
			}, this::showErro);
		}
	}

	private void populateForm(Cliente c) {
		newCliente = c;
		fieldNome.setText(nz(c.getNome()));
		fieldTelefone.setText(nz(c.getTelefone()));
		fieldEndereco.setText(nz(c.getEndereco()));
		comboContato.setValue(c.getPrefContato() == null ? Contato.WHATSAPP : c.getPrefContato());
		comboPagamento.setValue(c.getPrefPagamento() == null ? Pagamento.PIX : c.getPrefPagamento());
		fieldObservacao.setText(nz(c.getObservacao()));
	}

	private void clean() {
		newCliente = null;
		tabela.getSelectionModel().clearSelection();
		fieldNome.clear();
		fieldTelefone.clear();
		fieldEndereco.clear();
		comboContato.setValue(Contato.WHATSAPP);
		comboPagamento.setValue(Pagamento.PIX);
		fieldObservacao.clear();
	}

	private static String nz(String s) {
		return s == null ? "" : s;
	}

	private static String textOrNull(String s) {
		return (s == null || s.isBlank()) ? null : s;
	}

	private void showErro(Throwable t) {
		String msg = (t instanceof DataAccessException)
				? "Erro de acesso ao banco: " + t.getMessage()
				: "Erro: " + (t == null ? "desconhecido" : t.getMessage());
		t.printStackTrace();
		new Alert(Alert.AlertType.ERROR, msg).showAndWait();
	}
}
