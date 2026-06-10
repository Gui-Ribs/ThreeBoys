package com.threeboys.presentation.boundary;

import java.util.List;
import java.util.Optional;

import com.threeboys.application.control.ClienteControl;
import com.threeboys.domain.model.Cliente;
import com.threeboys.infrastructure.database.jdbc.helpers.DataAccessException;
import com.threeboys.presentation.navigation.SceneManager;
import com.threeboys.presentation.util.Tasks;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
 
        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.addRow(0, new Label("Nome"), fieldNome);
        form.addRow(1, new Label("Telefone"), fieldTelefone);
        form.addRow(2, new Label("Endereço"), fieldEndereco);
        form.addRow(3, new Label("Observação"), fieldObservacao);
 
        Button voltar = new Button("‹ Painel");
        voltar.setOnAction(e -> scenes.dashboard());
 
        Button novo = new Button("Novo");
        novo.setOnAction(e -> limparForm());
 
        Button salvar = new Button("Salvar");
        salvar.setOnAction(e -> salve());
 
        Button remover = new Button("Remover");
        remover.setOnAction(e -> removeSelected());
        remover.disableProperty().bind(
                tabela.getSelectionModel().selectedItemProperty().isNull());
 
        HBox acoes = new HBox(8, voltar, novo, salvar, remover);
 
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
 
        tabela.getColumns().addAll(List.of(colNome, colTelefone, colEndereco));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // TODO: Atualizar, não recomendo usar, é só teste
 
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                preencherForm(novo);
            }
        });
    }
 
    private void load() {
        Tasks.run(control::list, clientes::setAll, this::showErro);
    }
 
    private void salve() {
        Cliente cliente = new Cliente();
        if (newCliente != null) {
            cliente.setId(newCliente.getId());
        }
        cliente.setNome(fieldNome.getText());
        cliente.setTelefone(fieldTelefone.getText());
        cliente.setEndereco(textoOuNull(fieldEndereco.getText()));
        cliente.setObservacao(textoOuNull(fieldObservacao.getText()));
 
        boolean novo = (newCliente == null);
        Tasks.run(
                () -> novo ? control.save(cliente) : control.update(cliente),
                salvo -> {
                    limparForm();
                    load();
                },
                this::showErro); // validação do Control nome & telefone cai aqui como um Alert
    }
 
    private void removeSelected() {
        Cliente selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Remover \"" + nz(selecionado.getNome()) + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar remoção");
 
        Optional<ButtonType> resposta = confirm.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            Tasks.runVoid(() -> control.delete(selecionado.getId()),
                    () -> {
                        limparForm();
                        load();
                    },
                    this::showErro);
        }
    }
 
    private void preencherForm(Cliente c) {
        newCliente = c;
        fieldNome.setText(nz(c.getNome()));
        fieldTelefone.setText(nz(c.getTelefone()));
        fieldEndereco.setText(nz(c.getEndereco()));
        fieldObservacao.setText(nz(c.getObservacao()));
    }
 
    private void limparForm() {
        newCliente = null;
        tabela.getSelectionModel().clearSelection();
        fieldNome.clear();
        fieldTelefone.clear();
        fieldEndereco.clear();
        fieldObservacao.clear();
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
 
    private static String textoOuNull(String s) {
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
