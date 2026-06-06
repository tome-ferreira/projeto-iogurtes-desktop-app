package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.utils.AppAware;
import javafx.fxml.FXML;

/**
 * Controller for Stock.fxml.
 *
 * <p>Página em branco, apenas com a estrutura base.
 */
public class StockController implements AppAware {

    @FXML private Sidebar sidebarController;

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    @FXML
    public void initialize() {
        // Inicialização em branco para já
    }
}
