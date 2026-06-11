package com.threeboys.presentation.navigation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class DashboardBoundary {

	SceneManager scenes;
	private final VBox root = new VBox(16);

	public DashboardBoundary(SceneManager scenes) {
		this.scenes = scenes;
		show();
	}

	public Parent getView() {
		return root;
	}

	private void show() {
		Label titulo = new Label("Three Boys — Olá, " + scenes.usuarioLogado());
		titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		TilePane shortcuts = new TilePane(12, 12);
		Button exit = new Button("Sair");
		exit.setOnAction(e -> scenes.logout());
		exit.setPrefSize(80, 40);

		shortcuts.setPrefColumns(2);
		shortcuts.setAlignment(Pos.CENTER);
		shortcuts.getChildren().addAll(shortcut("Clientes", scenes::clientes), shortcut("Pedidos", scenes::pedidos),
				shortcut("Produto", scenes::produto), shortcut("Material", scenes::material));

		root.setPadding(new Insets(24));
		root.setAlignment(Pos.TOP_CENTER);
		VBox.setMargin(shortcuts, new Insets(120));
		root.getChildren().addAll(titulo, shortcuts, exit);
	}

	private Button shortcut(String texto, Runnable acao) {
		Button b = new Button(texto);
		b.setPrefSize(160, 80);
		b.setMaxWidth(Double.MAX_VALUE);
		b.setOnAction(e -> acao.run());
		return b;
	}
}
