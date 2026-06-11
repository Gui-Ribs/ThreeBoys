package com.threeboys.presentation.boundary;

import com.threeboys.application.control.LoginControl;
import com.threeboys.presentation.navigation.SceneManager;
import com.threeboys.presentation.util.Tasks;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginBoundary {

    private final LoginControl control;
    private final SceneManager scenes;

    private final TextField email = new TextField();
    private final PasswordField senha = new PasswordField();
    private final Button entrar = new Button("Entrar");
    private final Label erro = new Label();

    private final StackPane root = new StackPane();

    public LoginBoundary(LoginControl control, SceneManager scenes) {
        this.control = control;
        this.scenes = scenes;
        show();
    }

    public Region getView() {
        return root;
    }

    private void show() {
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;");

        Label titulo = new Label("3Boys");
        titulo.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitulo = new Label("Acesse sua conta");
        subtitulo.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #666666;"
        );

        email.setPromptText("admin@3boys.com");
        email.setMaxWidth(Double.MAX_VALUE);
        email.setPrefHeight(36);
        email.setOnAction(e -> senha.requestFocus());

        senha.setPromptText("Senha");
        senha.setMaxWidth(Double.MAX_VALUE);
        senha.setPrefHeight(36);
        senha.setOnAction(e -> authenticate());

        entrar.setDefaultButton(true);
        entrar.setMaxWidth(Double.MAX_VALUE);
        entrar.setPrefHeight(38);
        entrar.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        entrar.setOnAction(e -> authenticate());

        erro.setStyle(
                "-fx-text-fill: #b00020;" +
                "-fx-font-size: 12px;"
        );
        erro.setWrapText(true);
        erro.setVisible(false);
        erro.managedProperty().bind(erro.visibleProperty());

        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(28));
        card.setMaxWidth(380);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #dddddd;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 3);"
        );

        card.getChildren().addAll(
                titulo,
                subtitulo,
                space(8),
                field("Email", email),
                field("Senha", senha),
                space(4),
                entrar,
                erro
        );

        root.getChildren().setAll(card);
    }

    private VBox field(String label, TextField input) {
        Label lbl = new Label(label);
        lbl.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #333333;"
        );

        VBox box = new VBox(4);
        box.getChildren().addAll(lbl, input);

        return box;
    }

    private Region space(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        region.setPrefHeight(height);
        region.setMaxHeight(height);
        return region;
    }

    public void authenticate() {
        erro.setVisible(false);
        erro.setText("");

        String e = email.getText() == null ? "" : email.getText().trim();
        String s = senha.getText() == null ? "" : senha.getText();

        if (e.isBlank() || s.isBlank()) {
            erro.setText("Informe email e senha.");
            erro.setVisible(true);
            return;
        }

        entrar.setDisable(true);
        entrar.setText("Entrando...");

        Tasks.run(
                () -> control.authenticate(e, s),
                usuario -> {
                    entrar.setDisable(false);
                    entrar.setText("Entrar");
                    senha.clear();
                    scenes.dashboard();
                },
                t -> {
                    entrar.setDisable(false);
                    entrar.setText("Entrar");

                    String mensagem = t == null || t.getMessage() == null
                            ? "Falha ao entrar."
                            : t.getMessage();

                    erro.setText(mensagem);
                    erro.setVisible(true);
                }
        );
    }
}