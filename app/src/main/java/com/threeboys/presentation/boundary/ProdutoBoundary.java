package com.threeboys.presentation.boundary;

import com.threeboys.application.control.ProdutoControl;
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
import javafx.scene.layout.Pane;
import javafx.util.converter.NumberStringConverter;

import java.util.Optional;

public class ProdutoBoundary {

    private ProdutoControl controle;
    private SceneManager scenes;

    private TextField txtNome = new TextField();
    private TextField txtTamanho = new TextField();
    private ComboBox<String> cmbChocolate = new ComboBox<>();
    private ComboBox<String> cmbTipo = new ComboBox<>();
    private TextField txtPreco = new TextField();
    private TextField txtQtde = new TextField();
    private TextField txtObservacao = new TextField();
    private ObservableList<String> chocolates =
            FXCollections.observableArrayList("Ao Leite", "blend", "Branco", "50%");
    private ObservableList<String> tipos =
            FXCollections.observableArrayList("Normal", "Trufado", "Crocante", "Colher");

    private TableView<Produto> tabela =new TableView<>();

    public ProdutoBoundary(ProdutoControl controle, SceneManager scenes){
        this.controle = controle;
        this.scenes = scenes;
    }

    public Pane render(){
        BorderPane principal = new BorderPane();
        GridPane painelCampos = new GridPane();

        painelCampos.add(new Label("Nome"),0,0);
        painelCampos.add(txtNome,1,0);
        painelCampos.add(new Label("Tamanho"),2,0);
        painelCampos.add(txtTamanho,3,0);
        painelCampos.add(new Label("Chocolate"), 0,1);
        painelCampos.add(cmbChocolate,1,1);
        painelCampos.add(new Label("Tipo"),2,1);
        painelCampos.add(cmbTipo,3,1);
        painelCampos.add(new Label("Preço"),0,2);
        painelCampos.add(txtPreco,1,2);
        painelCampos.add(new Label("Qtde"),2,2);
        painelCampos.add(txtQtde,3,2);
        painelCampos.add(new Label("Observação"),0,3);
        painelCampos.add(txtObservacao,1,3);

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setOnAction((e) -> {
            controle.salvar();
            new Alert(Alert.AlertType.INFORMATION, "Gravado com sucesso.");
        });
        painelCampos.add(btnSalvar,0,4);

        Button btnPesquisar = new Button("Pesquisar");
        btnPesquisar.setOnAction((e) -> {
            controle.pesquisar();
        });
        painelCampos.add(btnPesquisar,1,4);

        Button btnDeletar = new Button("Deletar");
        btnDeletar.setOnAction((e) -> {
            Produto p = controle.toModel();
            if(p.getId() > 0) {
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
        painelCampos.add(btnDeletar,3,4);

        Button btnLimpar = new Button("Limpar");
        btnLimpar.setOnAction((e) -> {
            controle.limparCampos();
        });
        painelCampos.add(btnLimpar,4,4);

        Button btnVoltar = new Button("‹ Painel");
        btnVoltar.setOnAction(e -> scenes.dashboard());
        painelCampos.add(btnVoltar,5,4);

        cmbChocolate.setItems(chocolates);
        cmbTipo.setItems(tipos);

        Bindings.bindBidirectional(txtNome.textProperty(), controle.nomeProperty());
        Bindings.bindBidirectional(txtTamanho.textProperty(), controle.tamanhoProperty());
        Bindings.bindBidirectional(cmbChocolate.valueProperty(), controle.chocolateProperty());
        Bindings.bindBidirectional(cmbTipo.valueProperty(), controle.tipoProperty());
        Bindings.bindBidirectional(txtPreco.textProperty(), controle.precoProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(txtQtde.textProperty(), controle.qtdeProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(txtObservacao.textProperty(), controle.observacaoProperty());

        TableColumn<Produto, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(
                itemData -> new ReadOnlyStringWrapper(itemData.getValue().getNome())
        );
        TableColumn<Produto, String> colTamanho = new TableColumn<>("Tamanho");
        colTamanho.setCellValueFactory(
                itemData -> new ReadOnlyStringWrapper(itemData.getValue().getTamanho())
        );
        TableColumn<Produto, String> colChocolate = new TableColumn<>("Chocolate");
        colChocolate.setCellValueFactory(
                itemData -> new ReadOnlyStringWrapper(itemData.getValue().getChocolate())
        );
        TableColumn<Produto, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(
                itemData -> new ReadOnlyStringWrapper(itemData.getValue().getTipo())
        );
        TableColumn<Produto, Double> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(
                itemData -> new ReadOnlyDoubleWrapper(itemData.getValue().getPreco()).asObject()
        );
        TableColumn<Produto, Integer> colQtde = new TableColumn<>("Qtde");
        colQtde.setCellValueFactory(
                itemData -> new ReadOnlyIntegerWrapper(itemData.getValue().getQtde()).asObject()
        );
        TableColumn<Produto, String> colObservacao = new TableColumn<>("Observação");
        colObservacao.setCellValueFactory(
                itemData -> new ReadOnlyStringWrapper(itemData.getValue().getTipo())
        );

        tabela.getColumns().add(colNome);
        tabela.getColumns().add(colTamanho);
        tabela.getColumns().add(colChocolate);
        tabela.getColumns().add(colTipo);
        tabela.getColumns().add(colPreco);
        tabela.getColumns().add(colQtde);
        tabela.getColumns().add(colObservacao);

        tabela.setItems(controle.getLista());

        tabela.getSelectionModel().selectedItemProperty().addListener(
                (obj, antigo, novo) -> controle.fromModel(novo)
        );

        principal.setTop(painelCampos);
        principal.setCenter(tabela);
        return principal;
    }

    public Pane getView() {
        return render();
    }
}
