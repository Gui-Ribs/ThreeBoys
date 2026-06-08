package com.threeboys.presentation.boundary;

import com.threeboys.config.AppFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LoginBoundary {

	private final VBox root;

	public LoginBoundary() {

		Label titulo = new Label("3Boys!");

		TextField usuario = new TextField();
		usuario.setPromptText("Usuário");

		PasswordField senha = new PasswordField();
		senha.setPromptText("Senha");

		Button entrar = new Button("Entrar");
		entrar.setOnAction(e -> titulo.setText("Olá, " + usuario.getText() + "!"));

		root = new VBox(12, titulo, usuario, senha, entrar);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(24));
	}

	public Region getView() {
		return root;
	}
}
