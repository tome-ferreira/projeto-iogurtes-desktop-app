package com.gestaoiogurtes;

import atlantafx.base.theme.PrimerLight;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.NavigationHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GestaoIogurtes extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // Load the initial page via FXMLLoader
        var url = getClass().getResource("/fxml/paginas/PaginaLogin.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        // Inject app reference before the window is shown
        AppAware controller = loader.getController();
        controller.setApp(this);

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Gestão de Iogurtes");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void navegarParaLogin() {
        NavigationHelper.navigateTo(this, "/fxml/paginas/PaginaLogin.fxml");
    }

    public void navegarParaDashboard() {
        NavigationHelper.navigateTo(this, "/fxml/paginas/Dashboard.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getStage() {
        return stage;
    }
}