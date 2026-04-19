package com.gestaoiogurtes.paginas;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.utils.AppAware;
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

    // ── AppAware ──────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }
}