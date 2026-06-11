package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.NavigationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import com.gestaoiogurtes.utils.SessionManager;

/**
 * Controller for PaginaLogin.fxml.
 *
 * <p>All layout is declared in the FXML.  This class handles only
 * the login action (currently navigates straight to Dashboard).
 *
 * <p>Moved from the root {@code com.gestaoiogurtes} package into
 * {@code com.gestaoiogurtes.controllers} to match the pages folder convention.
 */
public class PaginaLogin implements AppAware {

    @FXML private TextField campoUser;
    @FXML private PasswordField campoPass;
    @FXML private Button btnEntrar;
    @FXML private ComboBox<String> comboRole;

    private GestaoIogurtes app;

    @FXML public void initialize() {
        comboRole.getItems().addAll(
            "ADMIN", "GESTOR", "CLIENTE", "FUNCIONARIO_OP", "FUNCIONARIO_MP"
        );
        comboRole.setValue("ADMIN"); // default
    }

    // ── AppAware ──────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
    }

    // ── FXML handler ──────────────────────────────────────────────
    @FXML
    private void handleEntrar() {
        String role = comboRole.getValue();
        if (role != null) {
            SessionManager.getInstance().setUserRole(role);
            switch (role) {
                case "ADMIN" -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardAdmin.fxml");
                case "GESTOR" -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardGestor.fxml");
                case "FUNCIONARIO_MP" -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardMp.fxml");
                case "FUNCIONARIO_OP" -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardFuncionarioOp.fxml");
                case "CLIENTE" -> NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
                default -> NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
            }
        } else {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
        }
    }
}
