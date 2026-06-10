package com.threeboys;

import com.threeboys.presentation.boundary.LoginBoundary;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) {
		LoginBoundary login = new LoginBoundary();

		Scene scene = new Scene(login.getView(), 1000, 600);
		stage.setTitle("3Boys");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
