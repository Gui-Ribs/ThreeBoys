package com.threeboys.presentation.boundary;

import com.threeboys.application.control.MaterialControl;
import com.threeboys.domain.model.Material;
import com.threeboys.domain.model.Produto;
import com.threeboys.presentation.navigation.SceneManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.converter.NumberStringConverter;

import java.util.Optional;

public class MaterialBoundary {

	private final MaterialControl controle;
	private final SceneManager scenes;

	private ObservableList<String> unidadesMedidas = FXCollections.observableArrayList("Kg", "g", "por pacote",
			"por rolo");
	private TextField txtNome = new TextField();
	private TextField txtQtde = new TextField();
	private ComboBox<String> cmbUnidadeMedida = new ComboBox<>();
	private TextField txtMarca = new TextField();
	private TextField txtPreco = new TextField();
	private TextField txtEstoque = new TextField();
	private TextField txtDescricao = new TextField();

	private TableView<Material> tabela = new TableView<>();



	public MaterialBoundary(MaterialControl controle, SceneManager scenes) {
		this.controle = controle;
		this.scenes = scenes;
	}

	public Pane render() {
		BorderPane principal = new BorderPane();
		GridPane painelCampos = new GridPane();

		cmbUnidadeMedida.setItems(unidadesMedidas);

		painelCampos.add(new Label("Nome"), 0, 0);
		painelCampos.add(txtNome, 1, 0);

		HBox hbQtde = new HBox();
		hbQtde.getChildren().addAll(new Label("Qtde"), txtQtde);
		painelCampos.add(hbQtde, 2, 0);

		HBox hbUm = new HBox();
		hbUm.getChildren().addAll(new Label("Unidade de Medida"), cmbUnidadeMedida);
		painelCampos.add(hbUm, 3, 0);

		painelCampos.add(new Label("Marca"), 0, 1);
		painelCampos.add(txtMarca, 1, 1);
		painelCampos.add(new Label("Preço"), 2, 1);
		painelCampos.add(txtPreco, 3, 1);
		painelCampos.add(new Label("Estoque"), 0, 2);
		painelCampos.add(txtEstoque, 1, 2);
		painelCampos.add(new Label("Descrição"), 2, 2);
		painelCampos.add(txtDescricao, 3, 2);

		Button btnSalvar = new Button("Salvar");
		btnSalvar.setOnAction((e) -> {
			controle.salvar();
			new Alert(Alert.AlertType.INFORMATION, "Salvado com sucesso").show();
		});
		painelCampos.add(btnSalvar, 0, 3);

		Button btnPesquisar = new Button("Pesquisar");
		btnPesquisar.setOnAction((e) -> {
			controle.pesquisar();
		});
		painelCampos.add(btnPesquisar, 1, 3);

		Button btnDeletar = new Button("Deletar");
		btnDeletar.setOnAction((e) -> {
			Material m = controle.toModel();
			if(m.getId() > 0) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
						"Apagar este item", ButtonType.YES, ButtonType.NO);
				alert.setTitle("Confirma Delete");

				Optional<ButtonType> resposta = alert.showAndWait();

				if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
					controle.deletar();
				}
			}else{
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selecione um Item");
				alert.setTitle("Aviso");
				alert.show();
			}
		});
		painelCampos.add(btnDeletar,2,3);

		Button btnVoltar = new Button("‹ Painel");
		btnVoltar.setOnAction(e -> scenes.dashboard());
		painelCampos.add(btnVoltar,3,3);

		Bindings.bindBidirectional(txtNome.textProperty(), controle.nomeProperty());
		Bindings.bindBidirectional(txtQtde.textProperty(), controle.qtdeProperty(), new NumberStringConverter());
		Bindings.bindBidirectional(cmbUnidadeMedida.valueProperty(), controle.unidadeMedidaProperty());
		Bindings.bindBidirectional(txtMarca.textProperty(), controle.marcaProperty());
		Bindings.bindBidirectional(txtPreco.textProperty(), controle.precoProperty(), new NumberStringConverter());
		Bindings.bindBidirectional(txtEstoque.textProperty(), controle.estoqueProperty(), new NumberStringConverter());
		Bindings.bindBidirectional(txtDescricao.textProperty(), controle.descricaoProperty());

		TableColumn<Material, String> colNome = new TableColumn<>("Nome");
		colNome.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getNome()));
		TableColumn<Material, Integer> colQtde = new TableColumn<>("Qtde");
		colQtde.setCellValueFactory(itemData -> new ReadOnlyIntegerWrapper(itemData.getValue().getQtde()).asObject());
		TableColumn<Material, String> colUm = new TableColumn<>("Unidade de Medida");
		colUm.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getUnidadeMedida()));
		TableColumn<Material, String> colMarca = new TableColumn<>("Marca");
		colMarca.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getMarca()));
		TableColumn<Material, Double> colPreco = new TableColumn<>("Preço");
		colPreco.setCellValueFactory(itemData -> new ReadOnlyDoubleWrapper(itemData.getValue().getPreco()).asObject());
		TableColumn<Material, Integer> colEstoque = new TableColumn<>("Estoque");
		colEstoque.setCellValueFactory(
				itemData -> new ReadOnlyIntegerWrapper(itemData.getValue().getEstoque()).asObject());
		TableColumn<Material, String> colDescricao = new TableColumn<>("Descrição");
		colDescricao.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getDescricao()));

		tabela.getColumns().add(colNome);
		tabela.getColumns().add(colQtde);
		tabela.getColumns().add(colUm);
		tabela.getColumns().add(colMarca);
		tabela.getColumns().add(colPreco);
		tabela.getColumns().add(colEstoque);
		tabela.getColumns().add(colDescricao);

		tabela.setItems(controle.getLista());

		principal.setTop(painelCampos);
		principal.setCenter(tabela);
		return principal;
	}

	public Pane getView() {
		return render();
	}
}
