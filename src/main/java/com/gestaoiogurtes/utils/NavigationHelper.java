package com.gestaoiogurtes.utils;

import com.gestaoiogurtes.GestaoIogurtes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Central navigation utility that loads an FXML file via classpath resource
 * and replaces the current scene root.
 *
 * <p>If the loaded controller implements {@link AppAware}, the {@code app}
 * reference is injected automatically before the root is placed in the scene.
 *
 * <p>Usage:
 * <pre>
 *   NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
 * </pre>
 */
public final class NavigationHelper {

    private NavigationHelper() {}

    /**
     * Loads {@code fxmlPath} from the classpath and sets its root as the
     * current scene root.  The controller's {@link AppAware#setApp} is called
     * immediately after loading so that navigation handlers have the app
     * reference before the user can interact with the new page.
     *
     * @param app      the main application instance
     * @param fxmlPath classpath-rooted path, e.g. {@code "/fxml/paginas/Dashboard.fxml"}
     */
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
