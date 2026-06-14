package com.gestaoiogurtes.utils;

import com.gestaoiogurtes.GestaoIogurtes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public final class NavigationHelper {

    private NavigationHelper() {
    }

    public static void navigateTo(GestaoIogurtes app, String fxmlPath) {
        try {
            var url = NavigationHelper.class.getResource(fxmlPath);
            if (url == null) {
                throw new IllegalArgumentException(
                        "FXML resource not found on classpath: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AppAware aware) {
                aware.setApp(app);
            }

            app.getStage().getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar FXML: " + fxmlPath, e);
        }
    }
}
