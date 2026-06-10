package com.threeboys;

import com.threeboys.config.AppFactory;
import com.threeboys.presentation.navigation.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) {


		AppFactory factory = AppFactory.getInstance();
		factory.validConnection();

		SceneManager scenes = new SceneManager(stage, factory);

		scenes.dashboard();

		stage.setTitle("3Boys");
		stage.setMinWidth(1000);
        stage.setMinHeight(800);
		stage.centerOnScreen();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
