package com.gestaoiogurtes.components.iogurtes;

import atlantafx.base.theme.Styles;
import com.gestaoiogurtes.model.IogurteVM;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.math.BigDecimal;

/**
 * Shared form-building utilities used by {@link CriarIogurteModal}
 * and {@link EditarIogurteModal}.
 * Package-private — not part of the public API.
 */
class IogurteFormHelper {

    /**
     * Builds the shared create/edit form, wires validation, and stores field
     * references in {@code dialog.getDialogPane().getUserData()}.
     */
    static VBox criarFormulario(IogurteVM iogurte, Dialog<?> dialog, ButtonType btnConfirmar) {
        var campoSku        = new TextField(iogurte != null ? iogurte.codigoSku : "");
        var campoNome       = new TextField(iogurte != null ? iogurte.nome : "");
        var campoDescricao  = new TextArea(iogurte != null ? iogurte.descricao : "");
        var campoValidade   = new TextField(iogurte != null ? String.valueOf(iogurte.validadeDias) : "");
        var campoPreco      = new TextField(iogurte != null ? iogurte.precoVenda.toPlainString() : "");
        var campoPrecoPorKg = new TextField(iogurte != null ? iogurte.precoPorKg.toPlainString() : "");
        var campoStock      = new TextField(iogurte != null ? String.valueOf(iogurte.stockAtual) : "0");
        var campoLote       = new TextField(iogurte != null ? String.valueOf(iogurte.quantidadeLote) : "1");
        var checkVisivel    = new CheckBox("Visível para clientes");
        checkVisivel.setSelected(iogurte == null || iogurte.visivelCliente);

        campoSku.setPromptText("ex: IOG-001");
        campoNome.setPromptText("Nome do produto");
        campoDescricao.setPromptText("Descrição opcional");
        campoDescricao.setPrefRowCount(3);
        campoDescricao.setWrapText(true);
        campoValidade.setPromptText("ex: 21");
        campoPreco.setPromptText("ex: 1.09");
        campoPrecoPorKg.setPromptText("ex: 4.36");
        campoStock.setPromptText("0");
        campoLote.setPromptText("1");

        // store field references so extrairFormulario can read them
        dialog.getDialogPane().setUserData(new Object[]{
                campoSku, campoNome, campoDescricao, campoValidade,
                campoPreco, campoPrecoPorKg, campoStock, campoLote, checkVisivel
        });

        // basic validation — disable confirm button when required fields are blank
        var btnNode = (Button) dialog.getDialogPane().lookupButton(btnConfirmar);
        if (btnNode != null) {
            Runnable validar = () -> btnNode.setDisable(
                    campoSku.getText().isBlank() || campoNome.getText().isBlank());
            campoSku.textProperty().addListener((o, a, n) -> validar.run());
            campoNome.textProperty().addListener((o, a, n) -> validar.run());
            validar.run();
        }

        var form = new VBox(12);
        form.setPadding(new Insets(8, 0, 8, 0));
        form.getChildren().addAll(
                campo("SKU *", campoSku),
                campo("Nome *", campoNome),
                campo("Descrição", campoDescricao),
                new HBox(16,
                        campo("Validade (dias)", campoValidade),
                        campo("Qtd. por lote", campoLote)),
                new HBox(16,
                        campo("Preço venda (€)", campoPreco),
                        campo("Preço por kg (€)", campoPrecoPorKg)),
                campo("Stock atual", campoStock),
                checkVisivel);
        return form;
    }

    /** Reads field values from dialog user-data and returns a new {@link IogurteVM}. */
    static IogurteVM extrairFormulario(Dialog<?> dialog) {
        var fields       = (Object[]) dialog.getDialogPane().getUserData();
        var sku          = ((TextField) fields[0]).getText().trim();
        var nome         = ((TextField) fields[1]).getText().trim();
        var descricao    = ((TextArea)  fields[2]).getText().trim();
        var validadeStr  = ((TextField) fields[3]).getText().trim();
        var precoStr     = ((TextField) fields[4]).getText().trim();
        var precoPkgStr  = ((TextField) fields[5]).getText().trim();
        var stockStr     = ((TextField) fields[6]).getText().trim();
        var loteStr      = ((TextField) fields[7]).getText().trim();
        var visivel      = ((CheckBox)  fields[8]).isSelected();

        return new IogurteVM(
                sku, nome, descricao,
                validadeStr.isBlank()  ? 0             : Integer.parseInt(validadeStr),
                precoStr.isBlank()     ? BigDecimal.ZERO : new BigDecimal(precoStr),
                precoPkgStr.isBlank()  ? BigDecimal.ZERO : new BigDecimal(precoPkgStr),
                stockStr.isBlank()     ? 0             : Integer.parseInt(stockStr),
                loteStr.isBlank()      ? 1             : Integer.parseInt(loteStr),
                visivel);
    }

    /** Creates a labelled input field wrapper. */
    static VBox campo(String labelTexto, javafx.scene.Node input) {
        var lbl = new Label(labelTexto);
        lbl.getStyleClass().addAll(Styles.TEXT_SMALL, Styles.TEXT_BOLD);
        var box = new VBox(4, lbl, input);
        if (input instanceof TextField tf)
            tf.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private IogurteFormHelper() {}
}
