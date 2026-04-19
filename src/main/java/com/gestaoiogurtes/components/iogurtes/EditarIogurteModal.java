package com.gestaoiogurtes.components.iogurtes;

import com.gestaoiogurtes.api.iogurtes.IIogurtesApiService;
import com.gestaoiogurtes.model.IogurteVM;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

/**
 * Self-contained modal for editing an existing iogurte.
 *
 * <pre>
 *   new EditarIogurteModal(iogurte, api, this::renderizarTabela).show(getScene().getWindow());
 * </pre>
 */
public class EditarIogurteModal {

    private final IogurteVM iogurte;
    private final IIogurtesApiService api;
    private final Runnable onEditado;

    public EditarIogurteModal(IogurteVM iogurte, IIogurtesApiService api, Runnable onEditado) {
        this.iogurte = iogurte;
        this.api = api;
        this.onEditado = onEditado;
    }

    public void show(Window owner) {
        var dialog = new Dialog<IogurteVM>();
        dialog.setTitle("Editar — " + iogurte.nome);
        dialog.initOwner(owner);

        var btnGuardar  = new ButtonType("Guardar",   ButtonBar.ButtonData.OK_DONE);
        var btnCancelar = new ButtonType("Cancelar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);
        dialog.getDialogPane().setContent(IogurteFormHelper.criarFormulario(iogurte, dialog, btnGuardar));
        dialog.getDialogPane().setPrefWidth(480);

        dialog.setResultConverter(bt -> {
            if (bt == btnGuardar)
                return IogurteFormHelper.extrairFormulario(dialog);
            return null;
        });

        dialog.showAndWait().ifPresent(editado -> {
            api.atualizar(iogurte, editado);
            onEditado.run();
        });
    }
}
