package com.gestaoiogurtes.components.iogurtes;

import com.gestaoiogurtes.api.iogurtes.IIogurtesApiService;
import com.gestaoiogurtes.model.IogurteVM;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

/**
 * Self-contained modal for creating a new iogurte.
 *
 * <pre>
 *   new CriarIogurteModal(api, this::renderizarTabela).show(getScene().getWindow());
 * </pre>
 */
public class CriarIogurteModal {

    private final IIogurtesApiService api;
    private final Runnable onCriado;

    public CriarIogurteModal(IIogurtesApiService api, Runnable onCriado) {
        this.api = api;
        this.onCriado = onCriado;
    }

    public void show(Window owner) {
        var dialog = new Dialog<IogurteVM>();
        dialog.setTitle("Novo iogurte");
        dialog.initOwner(owner);

        var btnCriar    = new ButtonType("Criar",     ButtonBar.ButtonData.OK_DONE);
        var btnCancelar = new ButtonType("Cancelar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCriar, btnCancelar);
        dialog.getDialogPane().setContent(IogurteFormHelper.criarFormulario(null, dialog, btnCriar));
        dialog.getDialogPane().setPrefWidth(480);

        dialog.setResultConverter(bt -> {
            if (bt == btnCriar)
                return IogurteFormHelper.extrairFormulario(dialog);
            return null;
        });

        dialog.showAndWait().ifPresent(novo -> {
            api.adicionar(novo);
            onCriado.run();
        });
    }
}
