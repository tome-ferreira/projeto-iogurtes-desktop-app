package com.gestaoiogurtes.utils;

import com.gestaoiogurtes.GestaoIogurtes;

/**
 * Implemented by FXML controllers that require the {@link GestaoIogurtes}
 * application reference to be injected after the FXMLLoader finishes
 * constructing the scene graph.
 */
public interface AppAware {
    void setApp(GestaoIogurtes app);
}
