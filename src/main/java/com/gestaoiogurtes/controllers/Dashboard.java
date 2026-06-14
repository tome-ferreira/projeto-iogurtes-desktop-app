package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.utils.AppAware;
import javafx.scene.control.Label;
import javafx.fxml.FXML;

public class Dashboard implements AppAware {

    @FXML
    private Sidebar sidebarController;
    @FXML
    private Label greetingLabel;

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