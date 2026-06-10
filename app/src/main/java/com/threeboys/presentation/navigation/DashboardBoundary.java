package com.threeboys.presentation.navigation;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class DashboardBoundary {


    SceneManager scenes;
    private final VBox root = new VBox(16);


    public DashboardBoundary(SceneManager scenes) {
        this.scenes = scenes;
    }

      public Parent getView() {
        return root;
    }
}
