package com.gestaoiogurtes.components.iogurtes;

import atlantafx.base.theme.Styles;
import com.gestaoiogurtes.api.iogurtes.IIogurtesApiService;
import com.gestaoiogurtes.model.IogurteVM;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

/**
 * Self-contained modal for confirming deletion of an iogurte.
 *
 * <pre>
 *   new ConfirmarApagarIogurteModal(iogurte, api, this::renderizarTabela).show(getScene().getWindow());
 * </pre>
 */
public class ConfirmarApagarIogurteModal {

    private final IogurteVM iogurte;
    private final IIogurtesApiService api;
    private final Runnable onApagado;

    public ConfirmarApagarIogurteModal(IogurteVM iogurte, IIogurtesApiService api, Runnable onApagado) {
        this.iogurte = iogurte;
        this.api = api;
        this.onApagado = onApagado;
    }

    public void show(Window owner) {
        var alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminação");
        alert.setHeaderText("Apagar \"" + iogurte.nome + "\"?");
        alert.setContentText("Esta ação não pode ser revertida.");
        alert.initOwner(owner);

        var btnApagar   = new ButtonType("Apagar",   ButtonBar.ButtonData.OK_DONE);
        var btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnApagar, btnCancelar);

        var nodeBtnApagar = (Button) alert.getDialogPane().lookupButton(btnApagar);
        nodeBtnApagar.getStyleClass().add(Styles.DANGER);

        alert.showAndWait().ifPresent(bt -> {
            if (bt == btnApagar) {
                api.remover(iogurte);
                onApagado.run();
            }
        });
    }
}
