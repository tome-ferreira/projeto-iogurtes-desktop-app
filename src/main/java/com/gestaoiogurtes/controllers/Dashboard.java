package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.utils.AppAware;
import javafx.scene.control.Label;
import javafx.fxml.FXML;

/**
 * Controller for Dashboard.fxml.
 *
 * <p>All layout is declared in the FXML.  The only responsibility of this
 * class is to propagate the {@link GestaoIogurtes} reference to the nested
 * {@link Sidebar} controller so that navigation buttons work correctly.
 *
 * <p>JavaFX convention: when {@code fx:id="sidebar"} is used on an
 * {@code <fx:include>}, the included controller is injected as
 * {@code sidebarController} (the fx:id value + "Controller").
 */

public class Dashboard implements AppAware {

    @FXML private Sidebar sidebarController;
    @FXML private Label greetingLabel;

    @FXML
    public void initialize() {
        int hour = java.time.LocalTime.now().getHour();
        String saudacao = hour < 12 ? "Bom dia" : hour < 19 ? "Boa tarde" : "Boa noite";

        String nome = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserName();
        String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();

        greetingLabel.setText(saudacao + ", " + nome + " — " + role);
    }

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }
}